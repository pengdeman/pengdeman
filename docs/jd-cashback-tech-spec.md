# 京东返利小程序 - 技术开发文档

## 文档信息

| 项 | 内容 |
|----|------|
| 文档名称 | 技术开发规格说明 |
| 版本 | v1.0 |
| 创建日期 | 2026-03-21 |
| 项目 | pengdeman Spring Boot 后端 |
| 依赖PRD | [jd-cashback-prd.md](./jd-cashback-prd.md) |

---

## 1. 技术架构说明

### 1.1 沿用现有技术栈

不改变现有架构，继续使用：

| 技术 | 说明 |
|------|------|
| Spring Boot 2.7.17 | Web框架 |
| Spring Data JPA | ORM持久化 |
| Spring Security + JWT | 认证授权 |
| H2 Database | 文件数据库 |
| Lombok | 简化代码 |
| OkHttp | HTTP客户端调用京东联盟API |

### 1.2 项目结构复用

继续使用现有的分层结构：
```
src/main/java/com/pengdeman/
├── config/          # 配置类（新增配置类）
├── controller/      # REST控制器（新增多个Controller）
├── dto/             # 数据传输对象（新增多个DTO）
├── exception/       # 异常处理（复用，新增异常如果需要）
├── model/           # JPA实体（新增实体，扩展现有实体）
├── repository/      # 数据访问层（新增Repository）
├── service/         # 业务逻辑层（新增Service，扩展现有）
└── util/            # 工具类（复用，新增工具类如果需要）
```

---

## 2. 数据库设计

### 2.1 新增实体

#### 2.1.1 `SystemConfigEntity` - 系统配置实体

**表名：** `system_config`

```java
@Entity
@Table(name = "system_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", unique = true, nullable = false, length = 100)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "config_desc", length = 200)
    private String configDesc;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
```

**对应Repository：**
`com.pengdeman.repository.SystemConfigRepository`

---

#### 2.1.2 `AdEntity` - 广告实体

**表名：** `ads`

```java
@Entity
@Table(name = "ads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (enabled == null) {
            enabled = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
```

**对应Repository：**
`com.pengdeman.repository.AdRepository`

---

#### 2.1.3 `MonthlyRankEntity` - 月度排行实体

**表名：** `monthly_rank`

```java
@Entity
@Table(name = "monthly_rank", indexes = {
    @Index(name = "idx_year_month_rebate", columnList = "yearMonth,totalRebate DESC")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyRankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_month", nullable = false, length = 7)
    private String yearMonth;  // 格式：YYYY-MM

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_rebate", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRebate;

    @Column(name = "rank_order")
    private Integer rankOrder;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (totalRebate == null) {
            totalRebate = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
```

**对应Repository：**
`com.pengdeman.repository.MonthlyRankRepository`

---

### 2.2 扩展现有实体

#### 2.2.1 `UserEntity` - 用户实体（修改）

**新增字段：**

```java
/**
 * 是否为管理员账号
 */
@Column(name = "is_admin")
private Boolean isAdmin;

/**
 * 用户名（用于管理员登录，普通用户可为空）
 */
@Column(name = "username", length = 50)
private String username;

/**
 * 密码（加密存储，用于管理员登录，普通用户可为空）
 */
@Column(name = "password", length = 100)
private String password;
```

**在构造/初始化时设置默认值：**
```java
@PrePersist
public void prePersist() {
    // ... 原有代码
    if (isAdmin == null) {
        isAdmin = false;
    }
}
```

---

#### 2.2.2 `ProductEntity` - 商品实体（修改）

现有实体已经存在，新增以下字段：

```java
/**
 * 京东SKU ID
 */
@Column(name = "sku_id", length = 50, unique = true)
private String skuId;

/**
 * 京东商品图片URL
 */
@Column(name = "product_image", length = 500)
private String productImage;

/**
 * 商品原价
 */
@Column(name = "original_price", precision = 10, scale = 2)
private BigDecimal originalPrice;

/**
 * 预估平台佣金
 */
@Column(name = "estimated_commission", precision = 10, scale = 2)
private BigDecimal estimatedCommission;

/**
 * 用户返利比例（默认20%）
 */
@Column(name = "user_rebate_rate", precision = 5, scale = 2)
private BigDecimal userRebateRate;

/**
 * 预估用户返利
 */
@Column(name = "estimated_user_rebate", precision = 10, scale = 2)
private BigDecimal estimatedUserRebate;

/**
 * CPS推广链接
 */
@Column(name = "cps_url", length = 500)
private String cpsUrl;

/**
 * 是否热门推荐
 */
@Column(name = "is_hot")
private Boolean isHot;

/**
 * 热门排序
 */
@Column(name = "hot_sort_order")
private Integer hotSortOrder;

// 初始化默认值
@PrePersist
public void prePersist() {
    // ... 原有代码
    if (userRebateRate == null) {
        userRebateRate = new BigDecimal("20.00");
    }
    if (isHot == null) {
        isHot = false;
    }
    if (hotSortOrder == null) {
        hotSortOrder = 0;
    }
}
```

> 说明：不新建 `jd_products` 表，直接复用扩展现有 `ProductEntity`

---

#### 2.2.3 `OrderEntity` - 订单实体（修改）

现有实体已经存在，新增以下字段：

```java
/**
 * 京东订单ID
 */
@Column(name = "jd_order_id", length = 50)
private String jdOrderId;

/**
 * 京东SKU ID
 */
@Column(name = "sku_id", length = 50)
private String skuId;

/**
 * 平台实际佣金（京东结算）
 */
@Column(name = "platform_commission", precision = 10, scale = 2)
private BigDecimal platformCommission;

/**
 * 用户返利金额
 */
@Column(name = "user_rebate_amount", precision = 10, scale = 2)
private BigDecimal userRebateAmount;

/**
 * 订单状态
 * PENDING - 待确认（已下单，未结算）
 * CONFIRMED - 已确认（佣金已结算）
 * PAID - 已返利（已入账）
 * CANCELLED - 已取消
 */
@Column(name = "order_status", length = 20)
@Enumerated(EnumType.STRING)
private OrderStatus orderStatus;

/**
 * 结算时间
 */
@Column(name = "settled_time")
private LocalDateTime settledTime;
```

**新增枚举类：** `com.pengdeman.model.OrderStatus`

```java
public enum OrderStatus {
    PENDING,    // 待确认
    CONFIRMED,  // 已确认佣金
    PAID,       // 已返利入账
    CANCELLED   // 已取消
}
```

---

#### 2.2.4 `WithdrawalEntity` - 提现实体（检查确认）

现有实体已满足需求，确保包含以下字段：
- `userId` - 用户ID
- `amount` - 提现金额
- `bankCardId` - 银行卡ID
- `status` - 状态（PENDING/APPROVED/PAID/REJECTED）
- `applyTime` - 申请时间
- `handleTime` - 处理时间
- `remark` - 备注（审核原因）

如果缺少 `remark` 字段，添加：
```java
/**
 * 审核备注
 */
@Column(name = "remark", length = 500)
private String remark;
```

---

### 2.3 配置项新增

在 `application.properties` 新增配置：

```properties
# ========== 京东联盟配置 ==========
jd.union.app-key=${JD_UNION_APP_KEY:}
jd.union.app-secret=${JD_UNION_APP_SECRET:}
jd.union.api-key=${JD_UNION_API_KEY:}
jd.union.promotion-id=${JD_UNION_PROMOTION_ID:}
jd.union.base-url=https://api.jd.com/routerjson

# ========== 默认管理员配置 ==========
admin.default.username=${ADMIN_DEFAULT_USERNAME:admin}
admin.default.password=${ADMIN_DEFAULT_PASSWORD:admin123}

# ========== 系统默认配置 ==========
system.default.min-withdrawal-amount=${SYSTEM_DEFAULT_MIN_WITHDRAWAL_AMOUNT:10.00}
system.default.user-rebate-rate=${SYSTEM_DEFAULT_USER_REBATE_RATE:20.00}
```

**新增配置类：** `com.pengdeman.config.JdUnionConfig.java`

```java
@Data
@Component
@ConfigurationProperties(prefix = "jd.union")
public class JdUnionConfig {
    private String appKey;
    private String appSecret;
    private String apiKey;
    private String promotionId;
    private String baseUrl = "https://api.jd.com/routerjson";
}
```

**新增配置类：** `com.pengdeman.config.AdminConfig.java`

```java
@Data
@Component
@ConfigurationProperties(prefix = "admin.default")
public class AdminConfig {
    private String username = "admin";
    private String password = "admin123";
}
```

---

## 3. 新增Service层

### 3.1 `JdUnionService` - 京东联盟API服务

**位置：** `com.pengdeman.service.JdUnionService`

**功能：**
1. 解析京东链接提取SKU ID
2. 调用京东联盟API获取商品信息和佣金
3. 生成推广CPS链接
4. 查询订单结算信息
5. 同步订单状态

**核心方法：**
```java
// 解析京东链接提取SKU
JdParseResult parseUrl(String originalUrl);

// 查询商品佣金信息
JdProductInfo getProductInfo(String skuId);

// 生成推广链接
String generateCpsUrl(String skuId);

// 查询订单列表
List<JdOrderResult> queryOrders(String startTime, String endTime);
```

**内部DTO：**
```java
// JdParseResult - 解析结果
// JdProductInfo - 商品信息
// JdOrderResult - 订单结果
```

---

### 3.2 `AdService` - 广告服务

**位置：** `com.pengdeman.service.AdService`

**功能：**
1. 获取启用的广告列表（给前端首页）
2. 管理员分页查询广告
3. 新增/编辑/删除广告
4. 启用/禁用广告

---

### 3.3 `SystemConfigService` - 系统配置服务

**位置：** `com.pengdeman.service.SystemConfigService`

**功能：**
1. 获取所有配置
2. 更新配置
3. 获取指定配置值（带缓存）
4. 获取最低提现金额
5. 获取用户返利比例

---

### 3.4 `MonthlyRankService` - 月度排行服务

**位置：** `com.pengdeman.service.MonthlyRankService`

**功能：**
1. 获取月度排行榜（分页）
2. 更新用户月度排行（订单结算时）
3. 每月1号自动清零重置排行
4. 重新计算排名

---

### 3.5 `AdminAuthService` - 管理员认证服务

**位置：** `com.pengdeman.service.AdminAuthService`

**功能：**
1. 管理员用户名密码登录
2. 生成JWT Token
3. 验证管理员权限
4. 初始化默认管理员账号（系统启动时）

---

### 3.6 扩展现有Service

需要扩展的现有Service：

| Service | 扩展内容 |
|---------|----------|
| `ProductService` | 新增方法：获取热门商品列表、设置/取消热门、解析京东链接添加商品 |
| `OrderService` | 新增方法：同步京东订单状态、结算返利到用户账户、获取用户订单列表 |
| `WithdrawalService` | 扩展：检查用户本月是否已申请提现，新增方法：管理员审核列表、审核通过/拒绝/标记打款 |
| `UserFinanceService` | 扩展：增加返利入账、冻结金额（提现申请）、解冻金额（审核拒绝） |

---

## 4. 新增Controller层

### 4.1 小程序端接口（用户端）

| Controller | 接口路径前缀 | 说明 |
|------------|-------------|------|
| `AdController` | `/api/ads` | 获取公开广告列表 |
| `JdController` | `/api/jd` | 京东链接解析转换 |
| `RankController` | `/api/rank` | 获取月度排行榜 |

修改扩展：
- `HomeController` - 首页聚合接口（可选，可直接调用多个接口）
- `ProductController` - 新增 `GET /api/products/hot` 获取热门商品

复用现有：
- `OrderController` - 用户订单列表已有基础，扩展返回更多信息
- `WithdrawalController` - 提现相关已有基础，增加检查 eligibility

### 4.2 后台管理接口（管理员端）

| Controller | 接口路径前缀 | 说明 |
|------------|-------------|------|
| `AdminAuthController` | `/api/admin/auth` | 管理员登录 |
| `AdminConfigController` | `/api/admin/config` | 系统配置管理 |
| `AdminAdController` | `/api/admin/ads` | 广告管理 |
| `AdminProductController` | `/api/admin/products` | 商品管理 + 热门商品管理 |
| `AdminWithdrawalController` | `/api/admin/withdrawals` | 提现审核管理 |
| `AdminUserController` | `/api/admin/users` | 用户管理 |
| `AdminStatisticsController` | `/api/admin/statistics` | 数据统计 |

所有 `/api/admin/**` 接口都需要：
- 管理员JWT认证
- 验证 `isAdmin = true` 权限

---

## 5. 安全配置修改

### 5.1 Spring Security配置

修改 `SecurityConfig.java`，添加：

1. `/api/admin/auth/login` - 放行（登录接口）
2. `/api/admin/**` - 需要认证且需要管理员权限
3. 自定义 `AuthorizationFilter` 支持管理员和普通用户两种JWT认证方式

### 5.2 JWT认证逻辑

沿用现有的 `JwtUtil`，但在解析Token后需要：
- 如果访问 `/api/admin/**`，必须验证用户 `isAdmin = true`
- 否则返回403无权限

---

## 6. 定时任务

新增 `com.pengdeman.config.ScheduledConfig` 开启定时任务。

### 6.1 订单同步任务

```
每天凌晨2点执行
```
- 同步最近30天京东订单
- 更新订单状态
- 对于已结算订单，将返利入账用户账户
- 更新月度排行榜

### 6.2 月度排行重置

```
每月1日凌晨0点执行
```
- 创建新的一月排行榜记录

---

## 7. DTO定义清单

### 7.1 用户端DTO

| DTO | 说明 |
|-----|------|
| `JdParseRequest` | 解析京东链接请求 |
| `JdParseResponse` | 解析京东链接响应 |
| `AdDTO` | 广告信息DTO |
| `RankUserDTO` | 排行用户DTO |
| `ProductWithRebateDTO` | 商品带返利信息DTO |

### 7.2 管理端DTO

| DTO | 说明 |
|-----|------|
| `AdminLoginRequest` | 管理员登录请求 |
| `AdminLoginResponse` | 管理员登录响应 |
| `SystemConfigDTO` | 系统配置DTO |
| `AdCreateRequest` | 创建广告请求 |
| `AdUpdateRequest` | 更新广告请求 |
| `ProductCreateRequest` | 创建商品请求 |
| `ProductUpdateRequest` | 更新商品请求 |
| `WithdrawalAuditRequest` | 提现审核请求 |
| `StatisticsOverviewDTO` | 概览统计DTO |

### 7.3 京东联盟DTO

| DTO | 说明 |
|-----|------|
| `JdUnionResponse` | 京东联盟通用响应 |
| `JdProductDetail` | 京东商品详情 |
| `JdCommissionInfo` | 佣金信息 |
| `JdOrderInfo` | 订单信息 |

---

## 8. 开发步骤顺序

建议按以下顺序开发：

### Phase 1: 基础数据模型和配置
1. [ ] 创建 `SystemConfigEntity` + `SystemConfigRepository` + `SystemConfigService`
2. [ ] 创建 `AdEntity` + `AdRepository` + `AdService`
3. [ ] 创建 `MonthlyRankEntity` + `MonthlyRankRepository` + `MonthlyRankService`
4. [ ] 扩展 `UserEntity` 添加管理员字段
5. [ ] 扩展 `ProductEntity` 添加京东商品字段
6. [ ] 扩展 `OrderEntity` 添加京东订单字段
7. [ ] 添加京东联盟配置类 `JdUnionConfig`
8. [ ] 添加管理员配置类 `AdminConfig`

### Phase 2: 管理员认证
1. [ ] 初始化默认管理员（ApplicationReadyEvent监听）
2. [ ] `AdminAuthService` 管理员登录
3. [ ] 修改SecurityConfig支持管理员认证
4. [ ] `AdminAuthController` 登录接口
5. [ ] 权限拦截验证管理员身份

### Phase 3: 首页基础功能
1. [ ] `AdController` 获取公开广告列表
2. [ ] `AdminAdController` 广告CRUD管理接口
3. [ ] `ProductService` 扩展获取热门商品列表
4. [ ] `ProductController` 添加 `/api/products/hot` 接口

### Phase 4: 京东链接转换核心功能
1. [ ] `JdUnionService` 链接解析SKU
2. [ ] `JdUnionService` 调用京东联盟API查询商品佣金
3. [ ] 计算用户返利（平台佣金 × 20%）
4. [ ] 生成CPS推广链接
5. [ ] `JdController` `POST /api/jd/parse` 接口

### Phase 5: 排行榜功能
1. [ ] `RankController` `GET /api/rank/monthly` 获取月度排行
2. [ ] 定时任务创建新月份，更新排名

### Phase 6: 订单同步和返利结算
1. [ ] `JdUnionService` 查询京东订单接口
2. [ ] 定时任务同步订单状态
3. [ ] 订单结算后返利入账用户账户
4. [ ] 更新月度排行榜统计

### Phase 7: 提现功能完善（复用现有，扩展检查）
1. [ ] `WithdrawalService` 扩展检查每月只能申请一次
2. [ ] 读取系统配置最低提现金额
3. [ ] `WithdrawalController` 添加 eligibility 检查接口

### Phase 8: 后台管理功能
1. [ ] `AdminConfigController` 系统配置CRUD
2. [ ] `AdminProductController` 商品管理 + 热门商品管理
3. [ ] `AdminWithdrawalController` 提现审核、通过/拒绝/标记打款
4. [ ] `AdminUserController` 用户列表查询、详情、禁用/启用
5. [ ] `AdminStatisticsController` 概览数据统计

### Phase 9: 测试和优化
1. [ ] 测试核心流程：链接解析 → 用户下单 → 同步结算 → 返利入账 → 提现申请 → 审核打款
2. [ ] 添加必要日志
3. [ ] 错误处理和异常提示

---

## 9. 可复用现有代码清单

| 现有代码 | 复用方式 |
|---------|----------|
| `WeChatService` | 微信登录复用不变 |
| `UserEntity` / `UserRepository` | 扩展字段后复用 |
| `UserFinanceEntity` / `UserFinanceService` | 资金账户逻辑完全复用 |
| `ProductEntity` / `ProductService` / `ProductController` | 扩展字段和新增方法复用 |
| `OrderEntity` / `OrderService` / `OrderController` | 扩展字段复用 |
| `WithdrawalEntity` / `WithdrawalService` / `WithdrawalController` | 扩展业务检查逻辑复用 |
| `BankCardEntity` / `BankCardService` / `BankCardController` | 完全复用 |
| `JwtUtil` | JWT生成验证逻辑复用 |
| `SecurityConfig` | 修改后复用 |
| 全局异常处理 | 完全复用 |

---

## 10. 京东联盟API调用说明

### 10.1 需要调用的接口

1. **`jd.union.open.goods.price.query`** - 查询商品价格
2. **`jd.union.open.goods.commission.query`** - 查询商品佣金
3. **`jd.union.open.promotion.bysubunionid.get`** - 生成推广链接
4. **`jd.union.open.order.query`** - 查询订单

### 10.2 签名方式
京东联盟API需要MD5签名，按照官方文档生成签名。

### 10.3 使用OkHttp调用
项目已经依赖OkHttp，直接复用，不需要新增依赖。

---

## 11. 默认系统配置初始值

| 配置Key | 默认值 | 说明 |
|---------|--------|------|
| `min_withdrawal_amount` | 10.00 | 最低提现金额 |
| `user_rebate_rate` | 20.00 | 用户返利比例（%） |
| `site_enabled` | true | 站点是否启用 |
| `announcement` |  | 系统公告 |

系统启动时自动初始化这些默认配置。

---

## 12. 业务规则代码实现

### 12.1 用户返利计算
```java
// platformCommission = 京东给平台的佣金
// userRebateRate = 配置的返利比例（默认20%）
BigDecimal userRebate = platformCommission.multiply(userRebateRate)
    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
```

### 12.2 每月只能提现一次检查
```java
// 查询当前用户本月是否已有PENDING/APPROVED/PAID状态的提现申请
LocalDate now = LocalDate.now();
LocalDate startOfMonth = now.withDayOfMonth(1);
LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
boolean hasApplied = withdrawalRepository.existsByUserIdAndApplyTimeBetweenAndStatusNot(
    userId,
    startOfMonth.atStartOfDay(),
    endOfMonth.atTime(23, 59, 59),
    WithdrawalStatus.REJECTED
);
```

### 12.3 提现金额冻结
用户提交提现申请后：
- 从 `availableBalance` 扣除提现金额 → 加到 `frozenBalance`

审核通过并打款后：
- 从 `frozenBalance` 扣除提现金额 → 加到 `totalWithdrawn`

审核拒绝后：
- 从 `frozenBalance` 扣除提现金额 → 加回 `availableBalance`

---

## 13. 依赖检查

项目现有Maven依赖已满足需求，不需要新增：
- ✅ Spring Boot Web
- ✅ Spring Data JPA
- ✅ Spring Security
- ✅ JWT
- ✅ OkHttp
- ✅ Lombok
- ✅ H2

---

## 14. 文件清单（新增/修改）

### 新增文件列表

```
src/main/java/com/pengdeman/
├── config/
│   ├── JdUnionConfig.java          # 新增
│   ├── AdminConfig.java            # 新增
│   └── ScheduledConfig.java        # 新增
├── controller/
│   ├── AdController.java            # 新增
│   ├── JdController.java           # 新增
│   ├── RankController.java         # 新增
│   ├── admin/
│   │   ├── AdminAuthController.java      # 新增
│   │   ├── AdminAdController.java        # 新增
│   │   ├── AdminConfigController.java    # 新增
│   │   ├── AdminProductController.java   # 新增
│   │   ├── AdminWithdrawalController.java # 新增
│   │   ├── AdminUserController.java      # 新增
│   │   └── AdminStatisticsController.java # 新增
├── dto/
│   ├── AdDTO.java                  # 新增
│   ├── JdParseRequest.java         # 新增
│   ├── JdParseResponse.java        # 新增
│   ├── RankUserDTO.java            # 新增
│   ├── admin/
│   │   ├── AdminLoginRequest.java  # 新增
│   │   ├── AdminLoginResponse.java # 新增
│   │   ├── SystemConfigDTO.java    # 新增
│   │   └── ... 其他admin DTO
├── model/
│   ├── SystemConfigEntity.java     # 新增
│   ├── AdEntity.java               # 新增
│   ├── MonthlyRankEntity.java      # 新增
│   ├── OrderStatus.java            # 新增（枚举）
├── repository/
│   ├── SystemConfigRepository.java # 新增
│   ├── AdRepository.java           # 新增
│   ├── MonthlyRankRepository.java  # 新增
├── service/
│   ├── JdUnionService.java         # 新增
│   ├── AdService.java              # 新增
│   ├── SystemConfigService.java    # 新增
│   ├── MonthlyRankService.java     # 新增
│   ├── AdminAuthService.java       # 新增
│   ├── JdUnionServiceImpl.java     # 新增（实现）
└── 原有文件修改：
    ├── model/UserEntity.java
    ├── model/ProductEntity.java
    ├── model/OrderEntity.java
    ├── model/WithdrawalEntity.java
    ├── config/SecurityConfig.java
    ├── service/ProductService.java
    ├── service/OrderService.java
    ├── service/WithdrawalService.java
    ├── controller/ProductController.java
    ├── controller/OrderController.java
    ├── resources/application.properties
```

---

**文档结束**
