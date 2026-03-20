# 项目介绍

## 概述

**pengdeman** 是一个**微信小程序电商返利平台**后端服务，提供完整的电商返利业务功能，包括微信小程序登录、商品管理、订单管理、用户资金管理、提现管理等核心业务流程。

项目采用标准的 Spring Boot 分层架构，开箱即用，一键部署。

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 2.7.17 | Java Web 框架，最后一个支持 Java 8 的稳定版本 |
| **Spring Data JPA** | 2.7.17 | ORM 持久化框架，简化数据库操作 |
| **Spring Security** | 2.7.17 | 安全认证框架 |
| **H2 Database** | - | 文件型嵌入式数据库，开发环境首选，重启数据不丢失 |
| **Hibernate** | 5.6.x | JPA 实现，自动建表/更新 |
| **JJWT** | 0.11.5 | JWT Token 生成和验证 |
| **OkHttp** | 4.10.0 | HTTP 客户端，用于调用微信 API |
| **Lombok** | - | 减少样板代码 |
| **Java** | 1.8+ | 开发语言 |
| **Maven** | - | 项目构建 |
| **HTTPS** | - | 生产环境默认启用 SSL 加密 |

---

## 项目架构

### 分层架构

项目采用标准的**MVC分层架构**，职责清晰：

```
com.pengdeman/
├── Application.java          # 主启动类
├── config/                   # 配置层 - 配置类、Bean定义
├── controller/               # 控制层 - 处理HTTP请求、参数校验
├── dto/                      # 数据传输对象 - 隔离内部模型和外部接口
├── exception/               # 异常处理 - 自定义异常、全局异常处理器
├── model/                    # 数据模型 - JPA实体，映射数据库表
├── repository/               # 数据访问层 - 继承JpaRepository，CRUD自动实现
├── service/                  # 业务逻辑层 - 处理业务逻辑、事务管理
└── util/                     # 工具类 - JWT工具等
```

### 架构层次职责

| 层次 | 职责 |
|------|------|
| **Config** | 读取配置参数，向Spring容器注册Bean |
| **Controller** | 接收HTTP请求，调用Service，返回JSON响应 |
| **DTO** | 请求/响应数据载体，避免暴露内部实体 |
| **Exception** | 统一异常处理，返回友好的错误信息 |
| **Model** | 数据库表映射，JPA注解配置 |
| **Repository** | 数据访问接口，Spring Data JPA自动实现 |
| **Service** | 实现业务逻辑，处理事务，调用多个Repository |
| **Util** | 通用工具方法 |

### 数据模型关系

```
┌─────────────────────────────────────────────────────────────┐
│                      UserEntity (users)                       │
└──────────┬──────────────────────────────────────────────────┘
           │
           ├─→ 1:1 → UserFinanceEntity (user_finance)    用户资金账户
           │
           ├─→ 1:N → OrderEntity (orders)                用户订单
           │
           ├─→ 1:N → BankCardEntity (bank_cards)         用户银行卡
           │
           └─→ 1:N → WithdrawalEntity (withdrawals)      用户提现记录

┌─────────────────────────────────────────────────────────────┐
│                    ProductEntity (products)                  │
└──────────┬──────────────────────────────────────────────────┘
           │
           └─→ N:1 → OrderEntity (orders)               商品订单

┌─────────────────────────────────────────────────────────────┐
│                CommissionRecordEntity (commission_records)   │
└──────────┬──────────────────────────────────────────────────┘
           │
           ├─→ N:1 → OrderEntity (orders)               关联订单
           │
           └─→ N:1 → UserEntity (users)                 关联用户

┌─────────────────────────────────────────────────────────────┐
│                  WithdrawalEntity (withdrawals)             │
└──────────┬──────────────────────────────────────────────────┘
           │
           └─→ N:1 → BankCardEntity (bank_cards)       提现银行卡
```

---

## 功能模块

### 1. 微信小程序认证授权

**接口：** `/api/auth/*`

**功能：**
- 微信小程序一键登录
- 通过 `code` 调用微信 `code2session` API 获取 `openid`
- 自动创建新用户/更新已有用户信息
- 自动创建用户资金账户
- 返回 JWT Token 用于后续接口认证

**数据库表：** `users`

**核心字段：** `openid`、`unionid`、`nickname`、`avatarUrl`、`gender`、`country`、`province`、`city`、`status`、`lastLoginTime`

---

### 2. 商品管理

**接口：** `/api/products/*`

**功能：**
- 商品分页列表查询
- 商品详情查询
- 按 SKU 查询商品
- 关键词搜索商品
- 创建/更新/删除商品（管理端）
- 根据京东链接查询商品信息
- 获取商品推广链接

**数据库表：** `products`

**核心字段：** `sku`、`title`、`description`、`price`、`commissionRate`、`image`、`categoryId`、`jdPrice`、`originalPrice`、`sales`、`stock`、`status`

**业务特点：** 对接京东联盟电商，用户通过推广链接购买后，平台获得佣金，用户获得返利。

---

### 3. 订单管理

**接口：** `/api/orders/*`

**功能：**
- 创建订单
- 获取用户订单列表（分页，按状态筛选）
- 获取订单详情
- 搜索订单
- 取消订单
- 确认收货

**数据库表：** `orders`

**核心字段：** `orderNo`、`userId`、`productId`、`sku`、`title`、`price`、`commission`、`userRebateAmount`、`quantity`、`totalAmount`、`promotionUrl`、`productImage`、`status`、`paymentMethod`、`orderType`

**订单状态流转：**
```
待支付 → 已支付 → 已发货 → 已收货 (完成)
   ↓
 已取消
```

佣金在用户确认收货后结算到用户资金账户。

---

### 4. 用户资金管理

**接口：** `/api/users/*`

**功能：**
- 获取当前登录用户信息
- 获取用户资金信息
- 获取可提现金额
- 获取订单统计数据
- 更新用户信息

**数据库表：** `user_finance`

**核心字段：** `userId`、`balance`、`totalIncome`、`withdrawableAmount`、`pendingWithdrawal`、`totalWithdrawn`、`orderCount`

---

### 5. 提现管理

**接口：** `/api/withdrawals/*`

**功能：**
- 用户提交提现申请
- 获取用户提现记录（分页，按状态筛选）
- 获取提现详情
- 管理员审核提现（批准/拒绝）
- 标记为已打款

**数据库表：** `withdrawals`

**提现状态流转：**
```
待审核 → 已批准 → 已打款
              ↓
            已拒绝
```

---

### 6. 银行卡管理

**接口：** `/api/bank-cards/*`

**功能：**
- 获取用户银行卡列表
- 添加银行卡
- 更新银行卡信息
- 删除银行卡

**数据库表：** `bank_cards`

**核心字段：** `userId`、`bankName`、`cardNumber`、`cardHolder`、`phone`、`isDefault`、`cardType`、`bankIcon`

---

### 7. 佣金记录

**实体：** `CommissionRecordEntity`

**数据库表：** `commission_records`

记录每笔佣金的明细，包含订单关联、用户关联、佣金金额、结算状态等，便于对账和查账。

---

## 认证安全

- **认证方式：** JWT Token
- **令牌有效期：** 7 天
- **Token 包含信息：** `userId`、`openid`
- **CSRF：** 已禁用（前后端分离场景）
- **Session：** 无状态（STATLESS）
- **密码加密：** BCrypt

---

## 第三方集成

- **微信小程序**：集成微信登录，通过 `code2session` 获取用户 `openid`
- **京东联盟**：计划集成，用于商品查询和推广链接生成

---

## 项目特点

| 特性 | 说明 |
|------|------|
| **开箱即用** | 单个可执行 JAR 包，无需外部依赖，一键启动 |
| **分层清晰** | 标准 MVC 分层，职责明确，代码易维护 |
| **RESTful API** | 遵循 REST 设计风格，统一返回格式 |
| **全局异常处理** | 统一捕获异常，返回友好的错误信息 |
| **参数验证** | 使用 Bean Validation 声明式参数校验 |
| **自动建表** | JPA hibernate.ddl-auto=update，启动自动更新表结构 |
| **数据持久化** | H2 文件数据库，重启应用数据不丢失 |
| **HTTPS 默认启用** | 生产环境默认启用 HTTPS，证书已配置 |
| **监控端点** | 集成 Spring Actuator，支持健康检查 |
| **易于扩展** | 模块划分清晰，新增功能方便 |

---

## 项目结构

```
pengdeman/
├── pom.xml                              # Maven 配置
├── src/
│   ├── main/
│   │   ├── java/com/pengdeman/
│   │   │   ├── Application.java        # 主启动类
│   │   │   ├── config/                 # 配置类
│   │   │   ├── controller/             # 控制器
│   │   │   ├── dto/                    # 数据传输对象
│   │   │   ├── exception/              # 异常处理
│   │   │   ├── model/                  # JPA 实体
│   │   │   ├── repository/             # 数据访问层
│   │   │   ├── service/                # 业务逻辑层
│   │   │   └── util/                   # 工具类
│   │   └── resources/
│   │       ├── application.properties  # 应用配置
│   │       ├── www.mmmmmmmm.vip.pfx   # SSL 证书
│   │       └── static/                 # 静态资源
│   └── test/                           # 测试代码
├── data/                               # H2 数据库文件
├── logs/                               # 日志文件
├── build.sh                            # 构建脚本
├── run.sh                              # 运行脚本
└── *.md                                # 项目文档
```

---

## 代码统计

| 包 | 文件数 |
|------|-------|
| config | 4 |
| controller | 8 |
| dto | 31 |
| exception | 8 |
| model | 8 |
| repository | 8 |
| service | 7 |
| util | 1 |
| **总计** | **65 个 Java 文件** |

---

## 快速开始

### 构建

```bash
./build.sh
# 或
mvn clean package -DskipTests
```

### 运行

```bash
./run.sh
# 或
java -jar target/pengdeman-1.0.0.jar
```

### 访问

- HTTPS: `https://your-domain.com`
- H2 控制台: `http://your-domain.com/h2-console`

---

## 配置说明

主要配置在 `src/main/resources/application.properties`：

```properties
# 服务器端口
server.port=443

# SSL/HTTPS 配置
server.ssl.enabled=true
server.ssl.key-store=classpath:www.mmmmmmmm.vip.pfx
server.ssl.key-store-type=PKCS12
server.ssl.key-store-password=your-password
server.ssl.key-alias=www.mmmmmmmm.vip
server.ssl.key-password=your-password

# 微信小程序配置
wechat.miniapp.app-id=your-app-id
wechat.miniapp.app-secret=your-app-secret

# 数据库配置
spring.datasource.url=jdbc:h2:file:./data/pengdeman;DB_CLOSE_DELAY=-1
```

---

## 相关文档

项目根目录已有详细文档：

- [`README.md`](./README.md) - 项目入门指南
- [`PROJECT_SUMMARY.md`](./PROJECT_SUMMARY.md) - 开发总结
- [`ECOMMERCE_API_GUIDE.md`](./ECOMMERCE_API_GUIDE.md) - 电商 API 设计指南
- [`MINIPROGRAM_INTEGRATION_GUIDE.md`](./MINIPROGRAM_INTEGRATION_GUIDE.md) - 小程序集成指南
- [`JAR_DEPLOY_GUIDE.md`](./JAR_DEPLOY_GUIDE.md) - JAR 部署指南

---

## 许可证

MIT License