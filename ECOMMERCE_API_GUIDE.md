# Pengdeman电商API接口开发指南

## 项目概述

本文档记录了pengdeman后端项目电商API接口的完整实现，包括数据模型、API接口、业务逻辑等详细信息。

## 目录结构

```
src/main/java/com/pengdeman/
├── model/                    # 实体类
│   ├── ProductEntity.java     # 商品实体
│   ├── OrderEntity.java       # 订单实体
│   ├── WithdrawalEntity.java  # 提现申请实体
│   ├── BankCardEntity.java    # 银行卡实体
│   ├── UserFinanceEntity.java # 用户资金实体
│   └── CommissionRecordEntity.java # 佣金记录实体
├── repository/               # 数据访问层
│   ├── ProductRepository.java
│   ├── OrderRepository.java
│   ├── WithdrawalRepository.java
│   ├── BankCardRepository.java
│   ├── UserFinanceRepository.java
│   └── CommissionRecordRepository.java
├── dto/                      # 数据传输对象
│   ├── PageResponse.java       # 通用分页响应
│   ├── ProductDTO.java        # 商品相关DTO
│   ├── OrderDTO.java          # 订单相关DTO
│   ├── OrderCreateRequest.java
│   ├── WithdrawalDTO.java
│   ├── WithdrawalCreateRequest.java
│   ├── BankCardDTO.java
│   ├── BankCardCreateRequest.java
│   ├── UserFinanceDTO.java
│   └── CommissionRecordDTO.java
├── service/                  # 业务逻辑层
│   ├── ProductService.java
│   ├── OrderService.java
│   ├── UserFinanceService.java
│   ├── WithdrawalService.java
│   └── BankCardService.java
├── controller/               # 控制器层
│   ├── ProductController.java
│   ├── OrderController.java
│   ├── UserFinanceController.java
│   ├── WithdrawalController.java
│   └── BankCardController.java
└── exception/               # 异常处理
    ├── ProductNotFoundException.java
    ├── OrderNotFoundException.java
    ├── WithdrawalException.java
    ├── BankCardException.java
    └── UserFinanceException.java
```

---

## 一、数据模型（Entity）

### 1.1 ProductEntity - 商品实体

**文件路径**: `src/main/java/com/pengdeman/model/ProductEntity.java`

商品实体用于存储京东商品的基本信息，包括SKU、价格、佣金比例等。

**主要字段**:
- `id` - 主键ID（自增）
- `sku` - 京东SKU（唯一）
- `title` - 商品标题
- `description` - 商品描述
- `price` - 商品价格
- `commissionRate` - 佣金比例（百分比）
- `imageUrl` - 商品主图URL
- `categoryId` - 商品分类ID
- `jdPrice` - 京东价
- `originalPrice` - 原价
- `salesCount` - 销量
- `stock` - 库存
- `status` - 状态（0-下架，1-上架）
- `createdAt` - 创建时间
- `updatedAt` - 更新时间

---

### 1.2 OrderEntity - 订单实体

**文件路径**: `src/main/java/com/pengdeman/model/OrderEntity.java`

订单实体用于存储用户通过推广链接购买商品的订单信息。

**主要字段**:
- `id` - 主键ID（自增）
- `orderNo` - 订单号（唯一）
- `userId` - 用户ID
- `productId` - 商品ID
- `sku` - 商品SKU
- `title` - 商品标题
- `price` - 商品价格
- `commission` - 佣金金额
- `userCommission` - 用户返利金额
- `quantity` - 购买数量
- `totalAmount` - 订单总金额
- `promotionLink` - 推广链接
- `productImage` - 商品图片
- `status` - 订单状态（1-待支付，2-已支付，3-已发货，4-已收货，5-已取消）
- `paymentMethod` - 支付方式
- `paymentTime` - 支付时间
- `shippingTime` - 发货时间
- `receiptTime` - 收货时间
- `cancelTime` - 取消时间
- `orderType` - 订单类型（jd-京东）
- `createdAt` - 创建时间
- `updatedAt` - 更新时间

---

### 1.3 WithdrawalEntity - 提现申请实体

**文件路径**: `src/main/java/com/pengdeman/model/WithdrawalEntity.java`

提现申请实体用于存储用户的提现申请记录。

**主要字段**:
- `id` - 主键ID（自增）
- `userId` - 用户ID
- `amount` - 提现金额
- `bankCardId` - 银行卡ID
- `status` - 状态（1-待审核，2-已批准，3-已打款，4-已拒绝）
- `auditTime` - 审核时间
- `payoutTime` - 打款时间
- `auditNote` - 审核备注
- `createdAt` - 创建时间
- `updatedAt` - 更新时间

---

### 1.4 BankCardEntity - 银行卡实体

**文件路径**: `src/main/java/com/pengdeman/model/BankCardEntity.java`

银行卡实体用于存储用户绑定的银行卡信息。

**主要字段**:
- `id` - 主键ID（自增）
- `userId` - 用户ID
- `bankName` - 银行名称
- `cardNumber` - 银行卡号
- `cardholderName` - 持卡人姓名
- `phoneNumber` - 预留手机号
- `isDefault` - 是否默认（0-否，1-是）
- `cardType` - 银行卡类型（储蓄卡、信用卡）
- `bankIcon` - 银行图标
- `createdAt` - 创建时间
- `updatedAt` - 更新时间

---

### 1.5 UserFinanceEntity - 用户资金实体

**文件路径**: `src/main/java/com/pengdeman/model/UserFinanceEntity.java`

用户资金实体用于存储用户的资金账户信息。

**主要字段**:
- `id` - 主键ID（自增）
- `userId` - 用户ID（唯一）
- `balance` - 账户余额
- `totalIncome` - 总收入
- `withdrawableAmount` - 可提现金额
- `orderCount` - 订单总数
- `pendingWithdrawal` - 待提现金额
- `totalWithdrawn` - 已提现总额
- `createdAt` - 创建时间
- `updatedAt` - 更新时间

---

### 1.6 CommissionRecordEntity - 佣金记录实体

**文件路径**: `src/main/java/com/pengdeman/model/CommissionRecordEntity.java`

佣金记录实体用于存储每笔订单的佣金结算记录。

**主要字段**:
- `id` - 主键ID（自增）
- `userId` - 用户ID
- `orderId` - 订单ID
- `productId` - 商品ID
- `commissionAmount` - 佣金金额
- `status` - 状态（1-待结算，2-已结算）
- `settledTime` - 结算时间
- `createdAt` - 创建时间
- `updatedAt` - 更新时间

---

## 二、数据传输对象（DTO）

### 2.1 PageResponse - 通用分页响应

**文件路径**: `src/main/java/com/pengdeman/dto/PageResponse.java`

通用分页响应DTO，用于所有需要分页的列表查询。

**主要字段**:
- `content` - 分页数据内容
- `page` - 当前页码（从0开始）
- `size` - 每页大小
- `totalElements` - 总记录数
- `totalPages` - 总页数
- `first` - 是否第一页
- `last` - 是否最后一页

---

### 2.2 ProductDTO - 商品响应DTO

**文件路径**: `src/main/java/com/pengdeman/dto/ProductDTO.java`

商品数据传输对象，用于返回商品信息。

**主要字段**:
- `id`, `sku`, `title`, `description` - 基本信息
- `price`, `commissionRate` - 价格和佣金比例
- `imageUrl`, `categoryId` - 图片和分类
- `jdPrice`, `originalPrice`, `salesCount`, `stock` - 京东相关信息
- `status` - 商品状态
- `commission` - 佣金金额（动态计算）
- `userCommission` - 用户返利金额（动态计算）
- `createdAt` - 创建时间

---

### 2.3 OrderDTO & OrderCreateRequest

**文件路径**:
- `src/main/java/com/pengdeman/dto/OrderDTO.java`
- `src/main/java/com/pengdeman/dto/OrderCreateRequest.java`

**OrderDTO主要字段**:
- 订单基本信息（id, orderNo, userId, productId）
- 商品信息（sku, title, price, productImage）
- 佣金信息（commission, userCommission）
- 数量和金额（quantity, totalAmount）
- 推广链接（promotionLink）
- 状态和时间（status, paymentMethod, 各种时间字段）
- 订单类型（orderType）

**OrderCreateRequest主要字段**:
- `sku`, `title`, `price`, `commissionRate`, `commission`, `userCommission`
- `quantity`, `promotionLink`, `productImage`
- `paymentMethod`, `orderType`

---

### 2.4 WithdrawalDTO & WithdrawalCreateRequest

**文件路径**:
- `src/main/java/com/pengdeman/dto/WithdrawalDTO.java`
- `src/main/java/com/pengdeman/dto/WithdrawalCreateRequest.java`

**WithdrawalDTO主要字段**:
- `id`, `userId`, `amount` - 基本信息
- `bankCardId`, `bankName`, `cardNumber`, `cardNumberMasked`, `cardholderName` - 银行卡信息
- `status`, `statusText` - 状态
- `auditTime`, `payoutTime`, `auditNote` - 审核信息
- `createdAt` - 创建时间

**WithdrawalCreateRequest主要字段**:
- `amount` - 提现金额
- `bankCardId` - 银行卡ID

---

### 2.5 BankCardDTO & BankCardCreateRequest

**文件路径**:
- `src/main/java/com/pengdeman/dto/BankCardDTO.java`
- `src/main/java/com/pengdeman/dto/BankCardCreateRequest.java`

**BankCardDTO主要字段**:
- 银行卡基本信息
- `cardNumberMasked` - 脱敏后的银行卡号
- `isDefault` - 是否默认
- `createdAt` - 创建时间

**BankCardCreateRequest主要字段**:
- `bankName`, `cardNumber`, `cardholderName`
- `phoneNumber`, `isDefault`, `cardType`, `bankIcon`

---

### 2.6 UserFinanceDTO

**文件路径**: `src/main/java/com/pengdeman/dto/UserFinanceDTO.java`

用户资金数据传输对象。

**主要字段**:
- `balance` - 余额
- `totalIncome` - 总收入
- `withdrawableAmount` - 可提现金额
- `orderCount` - 订单数
- `pendingWithdrawal` - 待提现金额
- `totalWithdrawn` - 已提现总额

---

## 三、数据访问层（Repository）

### 3.1 ProductRepository

**文件路径**: `src/main/java/com/pengdeman/repository/ProductRepository.java`

**主要方法**:
- `findBySku(String sku)` - 根据SKU查找商品
- `findByTitleContaining(String title, Pageable pageable)` - 根据标题模糊搜索（分页）
- `findByCategoryId(Long categoryId, Pageable pageable)` - 根据分类查找（分页）
- `findByStatus(Integer status, Pageable pageable)` - 根据状态查找（分页）
- `existsBySku(String sku)` - 判断SKU是否存在

---

### 3.2 OrderRepository

**文件路径**: `src/main/java/com/pengdeman/repository/OrderRepository.java`

**主要方法**:
- `findByOrderNo(String orderNo)` - 根据订单号查找
- `findByUserId(Long userId, Pageable pageable)` - 根据用户ID查找（分页）
- `findByUserIdAndStatus(Long userId, Integer status, Pageable pageable)` - 根据用户ID和状态查找（分页）
- `countByUserId(Long userId)` - 统计用户订单数
- `findByUserIdAndTitleContaining(Long userId, String title, Pageable pageable)` - 搜索用户订单（分页）
- `existsByOrderNo(String orderNo)` - 判断订单号是否存在

---

### 3.3 WithdrawalRepository

**文件路径**: `src/main/java/com/pengdeman/repository/WithdrawalRepository.java`

**主要方法**:
- `findByUserId(Long userId, Pageable pageable)` - 获取用户提现记录（分页）
- `findByUserIdAndStatus(Long userId, Integer status, Pageable pageable)` - 根据状态筛选（分页）
- `countByUserId(Long userId)` - 统计提现记录数
- `countByUserIdAndStatus(Long userId, Integer status)` - 统计指定状态的记录数

---

### 3.4 BankCardRepository

**文件路径**: `src/main/java/com/pengdeman/repository/BankCardRepository.java`

**主要方法**:
- `findByUserId(Long userId)` - 获取用户所有银行卡
- `findByUserIdAndIsDefault(Long userId, Integer isDefault)` - 获取默认银行卡
- `existsByUserIdAndCardNumber(Long userId, String cardNumber)` - 判断银行卡是否已添加

---

### 3.5 UserFinanceRepository

**文件路径**: `src/main/java/com/pengdeman/repository/UserFinanceRepository.java`

**主要方法**:
- `findByUserId(Long userId)` - 根据用户ID查找资金信息
- `existsByUserId(Long userId)` - 判断用户是否已创建资金账户

---

### 3.6 CommissionRecordRepository

**文件路径**: `src/main/java/com/pengdeman/repository/CommissionRecordRepository.java`

**主要方法**:
- `findByUserId(Long userId, Pageable pageable)` - 获取用户佣金记录（分页）
- `findByOrderId(Long orderId)` - 获取订单的佣金记录
- `countByUserId(Long userId)` - 统计佣金记录数
- `countByUserIdAndStatus(Long userId, Integer status)` - 统计指定状态的记录数

---

## 四、业务逻辑层（Service）

### 4.1 ProductService - 产品管理服务

**文件路径**: `src/main/java/com/pengdeman/service/ProductService.java`

**主要功能**:
1. `getAllProducts(int page, int size)` - 获取所有商品（分页）
2. `getProductById(Long id)` - 根据ID获取商品
3. `getProductBySku(String sku)` - 根据SKU获取商品
4. `searchProductsByTitle(String keyword, int page, int size)` - 搜索商品（分页）
5. `createProduct(ProductDTO productDTO)` - 创建商品
6. `updateProduct(Long id, ProductDTO productDTO)` - 更新商品
7. `deleteProduct(Long id)` - 删除商品
8. `getPromotionLink(String sku)` - 获取商品推广链接

**核心业务逻辑**:
- 自动计算佣金金额：`佣金 = 价格 × 佣金比例%`
- 用户返利：`用户返利 = 佣金 × 20%`
- 推广链接目前返回模拟值，实际应调用京东联盟API

---

### 4.2 OrderService - 订单管理服务

**文件路径**: `src/main/java/com/pengdeman/service/OrderService.java`

**主要功能**:
1. `createOrder(Long userId, OrderCreateRequest request)` - 创建订单
2. `getUserOrders(Long userId, Integer status, int page, int size)` - 获取用户订单列表（分页）
3. `getOrderById(Long userId, Long orderId)` - 获取订单详情
4. `searchUserOrders(Long userId, String keyword, int page, int size)` - 搜索订单
5. `cancelOrder(Long userId, Long orderId)` - 取消订单
6. `confirmReceipt(Long userId, Long orderId)` - 确认收货
7. `updateOrderStatus(Long orderId, Integer status)` - 更新订单状态（内部调用）

**核心业务逻辑**:
- **订单号生成**: `JD` + 时间戳
- **订单状态流转**:
  - 1（待支付）→ 2（已支付）→ 3（已发货）→ 4（已收货）
  - 1（待支付）→ 5（已取消）
- **自动创建/更新商品**: 创建订单时如果商品不存在会自动创建
- **佣金结算**: 订单确认收货后自动创建佣金记录并更新用户资金
- **用户资金更新**: 增加余额、总收入、可提现金额（80%的余额）

---

### 4.3 UserFinanceService - 用户资金管理服务

**文件路径**: `src/main/java/com/pengdeman/service/UserFinanceService.java`

**主要功能**:
1. `getUserFinance(Long userId)` - 获取用户资金信息（不存在则创建默认）
2. `getWithdrawableAmount(Long userId)` - 获取可提现金额
3. `addBalance(Long userId, BigDecimal amount)` - 增加用户余额
4. `deductBalance(Long userId, BigDecimal amount)` - 减少用户余额（用于提现）
5. `completeWithdrawal(Long userId, BigDecimal amount)` - 完成提现

**核心业务逻辑**:
- 自动创建默认资金账户
- 可提现金额 = 余额 × 80%
- 提现时：减少余额和可提现金额，增加待提现金额
- 提现完成时：减少待提现金额，增加已提现总额

---

### 4.4 WithdrawalService - 提现管理服务

**文件路径**: `src/main/java/com/pengdeman/service/WithdrawalService.java`

**主要功能**:
1. `createWithdrawal(Long userId, WithdrawalCreateRequest request)` - 创建提现申请
2. `getUserWithdrawals(Long userId, Integer status, int page, int size)` - 获取用户提现记录（分页）
3. `getWithdrawalById(Long userId, Long withdrawalId)` - 获取提现详情
4. `approveWithdrawal(Long withdrawalId, Boolean approved, String auditNote)` - 审核提现申请
5. `markAsPaid(Long withdrawalId)` - 标记为已打款

**核心业务逻辑**:
- 验证银行卡归属
- 验证可提现金额是否充足
- 审核通过：状态变更为已批准
- 审核拒绝：状态变更为已拒绝，恢复用户余额
- 标记已打款：状态变更为已打款
- 银行卡号脱敏：显示前4位和后4位，中间用星号

---

### 4.5 BankCardService - 银行卡管理服务

**文件路径**: `src/main/java/com/pengdeman/service/BankCardService.java`

**主要功能**:
1. `getUserBankCards(Long userId)` - 获取用户银行卡列表
2. `addBankCard(Long userId, BankCardCreateRequest request)` - 添加银行卡
3. `deleteBankCard(Long userId, Long bankCardId)` - 删除银行卡
4. `setDefaultBankCard(Long userId, Long bankCardId)` - 设置默认银行卡
5. `getDefaultBankCard(Long userId)` - 获取默认银行卡

**核心业务逻辑**:
- 防止重复添加同一张银行卡
- 只能有一张默认银行卡
- 删除默认银行卡时自动设置另一张为默认（如果有）
- 返回脱敏后的银行卡号

---

## 五、控制器层（Controller）

### 5.1 ProductController - 产品API

**文件路径**: `src/main/java/com/pengdeman/controller/ProductController.java`

**基础路径**: `/api/products`

**API接口列表**:

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/` | 获取所有商品（分页） |
| GET | `/{id}` | 获取商品详情 |
| GET | `/sku/{sku}` | 根据SKU获取商品 |
| GET | `/search` | 搜索商品（分页） |
| POST | `/` | 创建商品 |
| PUT | `/{id}` | 更新商品 |
| DELETE | `/{id}` | 删除商品 |
| POST | `/{id}/promotion-link` | 获取商品推广链接 |
| POST | `/sku/{sku}/promotion-link` | 根据SKU获取推广链接 |

---

### 5.2 OrderController - 订单API

**文件路径**: `src/main/java/com/pengdeman/controller/OrderController.java`

**基础路径**: `/api/orders`

**API接口列表**:

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/` | 创建订单 |
| GET | `/` | 获取用户订单列表（分页） |
| GET | `/{id}` | 获取订单详情 |
| GET | `/search` | 搜索订单（分页） |
| PUT | `/{id}/cancel` | 取消订单 |
| PUT | `/{id}/confirm-receipt` | 确认收货 |

**请求参数说明**:
- `userId` - 用户ID（注意：实际使用时应从JWT token中解析）
- `status` - 订单状态筛选（可选）
- `keyword` - 搜索关键词

---

### 5.3 UserFinanceController - 用户资金API

**文件路径**: `src/main/java/com/pengdeman/controller/UserFinanceController.java`

**基础路径**: `/api/users`

**API接口列表**:

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/{userId}/finance` | 获取用户资金信息 |
| GET | `/{userId}/withdrawable-amount` | 获取可提现金额 |

---

### 5.4 WithdrawalController - 提现管理API

**文件路径**: `src/main/java/com/pengdeman/controller/WithdrawalController.java`

**基础路径**: `/api/withdrawals`

**API接口列表**:

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/` | 创建提现申请 |
| GET | `/` | 获取用户提现记录（分页） |
| GET | `/{id}` | 获取提现详情 |
| PUT | `/{id}/approve` | 审核提现申请 |
| PUT | `/{id}/mark-paid` | 标记为已打款 |

---

### 5.5 BankCardController - 银行卡管理API

**文件路径**: `src/main/java/com/pengdeman/controller/BankCardController.java`

**基础路径**: `/api/bank-cards`

**API接口列表**:

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/` | 获取用户银行卡列表 |
| GET | `/default` | 获取默认银行卡 |
| POST | `/` | 添加银行卡 |
| DELETE | `/{id}` | 删除银行卡 |
| PUT | `/{id}/set-default` | 设置默认银行卡 |

---

## 六、异常处理

### 6.1 自定义异常类

| 异常类 | 描述 |
|--------|------|
| ProductNotFoundException | 商品未找到异常 |
| OrderNotFoundException | 订单未找到异常 |
| WithdrawalException | 提现操作异常 |
| BankCardException | 银行卡操作异常 |
| UserFinanceException | 用户资金操作异常 |

### 6.2 全局异常处理

**文件路径**: `src/main/java/com/pengdeman/exception/GlobalExceptionHandler.java`

已添加对以下异常的处理：
- `ProductNotFoundException` - 返回404状态码
- `OrderNotFoundException` - 返回404状态码
- `WithdrawalException` - 返回400状态码
- `BankCardException` - 返回400状态码
- `UserFinanceException` - 返回400状态码
- `IllegalStateException` - 返回400状态码

**错误响应格式**:
```json
{
    "status": 404,
    "error": "Order Not Found",
    "message": "订单不存在: 123",
    "timestamp": "2024-03-13T10:30:00",
    "details": null
}
```

---

## 七、订单状态说明

| 状态值 | 状态文本 | 说明 |
|--------|----------|------|
| 1 | 待支付 | 订单刚创建，等待用户支付 |
| 2 | 已支付 | 用户已完成支付 |
| 3 | 已发货 | 商家已发货 |
| 4 | 已收货 | 用户已确认收货，佣金结算 |
| 5 | 已取消 | 订单已取消 |

---

## 八、提现状态说明

| 状态值 | 状态文本 | 说明 |
|--------|----------|------|
| 1 | 待审核 | 提现申请已提交，等待审核 |
| 2 | 已批准 | 审核通过，准备打款 |
| 3 | 已打款 | 已完成打款 |
| 4 | 已拒绝 | 审核拒绝，余额已恢复 |

---

## 九、使用示例

### 9.1 创建订单示例

```bash
POST /api/orders?userId=1
Content-Type: application/json

{
    "sku": "10001",
    "title": "iPhone 15 Pro",
    "price": 9999.00,
    "commissionRate": 2.0,
    "commission": 199.98,
    "userCommission": 39.99,
    "quantity": 1,
    "promotionLink": "https://u.jd.com/xxxxx",
    "productImage": "https://img.yzcdn.cn/vant/ipad.jpeg",
    "paymentMethod": "微信支付",
    "orderType": "jd"
}
```

### 9.2 创建提现申请示例

```bash
POST /api/withdrawals?userId=1
Content-Type: application/json

{
    "amount": 100.00,
    "bankCardId": 1
}
```

---

## 十、后续优化建议

### 10.1 JWT认证集成

当前实现中，`userId`是通过请求参数传递的，实际使用时应该：

1. 完善SecurityConfig，添加JWT过滤器
2. 从Authorization header中解析token
3. 从token中提取userId，而不是从参数获取

### 10.2 京东联盟API集成

在ProductService中，当前getPromotionLink返回的是模拟值，应该：

1. 集成京东联盟SDK或直接调用API
2. 查询商品真实信息
3. 生成真实的推广链接
4. 配置京东联盟API密钥到application.properties

### 10.3 添加单元测试

为以下层添加测试用例：
- Service层：使用JUnit和Mockito
- Controller层：使用MockMvc
- Repository层：使用@DataJpaTest

### 10.4 性能优化

- 添加Redis缓存：缓存商品列表、热门商品等
- 数据库优化：添加索引
- 查询优化：使用JOIN查询代替N+1查询

### 10.5 支付集成

- 集成微信支付
- 集成支付宝支付
- 处理支付回调

### 10.6 API文档

集成Swagger/OpenAPI文档，方便API测试和调试。

---

## 附录

### A. 数据库表结构初始化SQL

```sql
-- 产品表
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    commission_rate DECIMAL(5,2) NOT NULL,
    image_url VARCHAR(500),
    category_id BIGINT,
    jd_price DECIMAL(10,2),
    original_price DECIMAL(10,2),
    sales_count INT DEFAULT 0,
    stock INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    sku VARCHAR(50) NOT NULL,
    title VARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    commission DECIMAL(10,2) NOT NULL,
    user_commission DECIMAL(10,2),
    quantity INT NOT NULL DEFAULT 1,
    total_amount DECIMAL(10,2) NOT NULL,
    promotion_link VARCHAR(1000),
    product_image VARCHAR(500),
    status TINYINT DEFAULT 1,
    payment_method VARCHAR(50),
    payment_time DATETIME,
    shipping_time DATETIME,
    receipt_time DATETIME,
    cancel_time DATETIME,
    order_type VARCHAR(20),
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- 其他表结构略...
```

### B. 配置示例

在application.properties中添加：

```properties
# 京东联盟API配置
jd.union.app-key=your-app-key
jd.union.app-secret=your-app-secret
jd.union.pid=your-position-id
```

---

## 总结

本文档详细记录了pengdeman后端项目电商API的完整实现，包括：

- 6个实体类的数据模型设计
- 6个Repository数据访问接口
- 11个DTO数据传输对象
- 5个Service业务逻辑服务
- 5个Controller控制器
- 5个自定义异常类
- 完整的全局异常处理

所有代码都遵循了Spring Boot最佳实践，使用构造函数注入、分层架构、事务管理等技术。
