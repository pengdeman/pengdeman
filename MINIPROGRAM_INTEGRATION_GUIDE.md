# 微信小程序后端API集成指南

## 概述

本指南说明如何将微信小程序与pengdeman Spring Boot后端API集成。

## 已完成的更新

### 1. API工具封装 (`utils/api.js`)

完全重写了API工具，包含以下功能：

- **通用请求封装**: `request`, `get`, `post`, `put`, `del`
- **微信登录**: `wxLogin`, `getCurrentUser`, `updateUser`
- **商品管理**: `getProduct`, `queryProductByUrl`, `getPromotionUrl`, `searchProducts`
- **订单管理**: `createOrder`, `getOrders`, `getOrderDetail`, `cancelOrder`, `confirmOrderReceipt`
- **资金管理**: `getUserFinance`, `getOrderStats`
- **提现管理**: `getWithdrawableAmount`, `createWithdrawal`, `getWithdrawals`
- **银行卡管理**: `getBankCards`, `addBankCard`, `updateBankCard`, `deleteBankCard`

### 2. 小程序入口更新 (`app.js`)

- 添加微信登录功能
- 自动登录逻辑
- 用户信息管理
- 全局错误处理
- 登出功能

### 3. 页面集成

| 页面 | 文件 | 功能 |
|------|------|------|
| 首页 | `pages/index/index.js` | 商品搜索、查询、复制推广链接 |
| 商品详情 | `pages/product/detail.js` | 商品详情、获取推广链接、创建订单 |
| 资金管理 | `pages/money/index.js` | 资金信息展示、用户信息更新 |
| 订单管理 | `pages/order/index.js` | 订单列表、订单操作、分页加载 |
| 提现管理 | `pages/withdraw/index.js` | 提现申请、银行卡选择 |

## API配置

### API基础地址

在 `utils/api.js` 中配置：

```javascript
const BASE_URL = 'http://localhost:8080/api'
```

**生产环境部署时需要修改为实际的服务器地址。**

### 微信小程序服务器域名配置

在微信公众平台后台需要配置以下域名：

- **request合法域名**: `https://your-server-domain.com`
- **uploadFile合法域名**: `https://your-server-domain.com` (如需上传)
- **downloadFile合法域名**: `https://your-server-domain.com` (如需下载)

## 微信登录流程

```
1. 小程序调用 wx.login() 获取 code
2. 小程序调用 wx.getUserProfile() 获取用户信息
3. 将 code 和用户信息发送到后端 /api/auth/wx-login
4. 后端调用微信API获取 openid 和 session_key
5. 后端查找或创建用户，生成JWT token
6. 后端返回用户信息和token
7. 小程序保存token到本地存储
```

## 请求认证

所有需要认证的API请求都需要在请求头中携带JWT token：

```
Authorization: Bearer {token}
```

`utils/api.js` 会自动处理这个。

## 错误处理

`app.js` 提供了全局错误处理方法 `handleError()`，可以处理：

- 网络错误
- Token过期（自动登出）
- 业务错误

## 本地存储数据结构

```javascript
{
  USER_INFO: {
    id: 1,
    openid: "xxx",
    nickname: "用户昵称",
    avatar: "头像URL",
    token: "JWT token",
    balance: 100.00,
    totalIncome: 200.00,
    withdrawableAmount: 80.00,
    orderCount: 5
  },
  BALANCE: 100.00,  // 兼容旧代码
  SEARCH_HISTORY: [...],
  WITHDRAW_RECORDS: [...],
  NOTIFICATIONS: [...]
}
```

## 后端API接口说明

### 认证接口

#### 微信登录
```
POST /api/auth/wx-login
Content-Type: application/json

{
  "code": "wx_login_code",
  "nickname": "用户昵称",
  "avatar": "头像URL",
  "gender": 1
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "jwt_token",
    "userId": 1,
    "openid": "xxx",
    "nickname": "用户昵称",
    "avatar": "头像URL",
    "isNewUser": false,
    "balance": 0.00,
    "totalIncome": 0.00,
    "withdrawableAmount": 0.00,
    "orderCount": 0
  }
}
```

### 商品接口

#### 通过URL查询商品
```
POST /api/products/query-by-url
Content-Type: application/json
Authorization: Bearer {token}

{
  "url": "https://item.jd.com/123456.html"
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "skuId": "123456",
    "title": "商品标题",
    "price": 999.00,
    "originalPrice": 1099.00,
    "image": "图片URL",
    "images": ["图片1", "图片2"],
    "commission": 20.00,
    "commissionRate": 2.0,
    "userCommission": 10.00,
    "shopName": "店铺名称",
    "sales": 1000,
    "coupon": {
      "amount": 50,
      "condition": 500,
      "name": "满500减50"
    }
  }
}
```

#### 获取商品推广链接
```
POST /api/products/{id}/promotion-link
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "promotionUrl": "https://u.jd.com/xxxxx",
    "shortUrl": "https://u.jd.com/xxxxx",
    "clickUrl": "https://u.jd.com/xxxxx"
  }
}
```

### 订单接口

#### 创建订单
```
POST /api/orders
Content-Type: application/json
Authorization: Bearer {token}

{
  "productId": 1,
  "productName": "商品名称",
  "productImage": "图片URL",
  "price": 999.00,
  "commission": 20.00,
  "userCommission": 10.00
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "orderNo": "JD202401010001",
    "userId": 1,
    "productId": 1,
    "productName": "商品名称",
    "productImage": "图片URL",
    "price": 999.00,
    "commission": 20.00,
    "userCommission": 10.00,
    "status": "pending",
    "orderTime": "2024-01-01T10:00:00"
  }
}
```

#### 获取订单列表
```
GET /api/orders?page=0&size=20&status=pending
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 5,
    "size": 20,
    "number": 0
  }
}
```

#### 取消订单
```
PUT /api/orders/{id}/cancel
Authorization: Bearer {token}
```

#### 确认收货
```
PUT /api/orders/{id}/confirm-receipt
Authorization: Bearer {token}
```

### 资金接口

#### 获取用户资金信息
```
GET /api/users/finance
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "balance": 100.00,
    "totalIncome": 200.00,
    "withdrawableAmount": 80.00,
    "orderCount": 5,
    "pendingWithdrawal": 20.00,
    "totalWithdrawn": 100.00
  }
}
```

#### 获取可提现金额
```
GET /api/users/withdrawable-amount
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "withdrawableAmount": 80.00
  }
}
```

### 提现接口

#### 创建提现申请
```
POST /api/withdrawals
Content-Type: application/json
Authorization: Bearer {token}

{
  "amount": 50.00,
  "bankCardId": 1
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "amount": 50.00,
    "fee": 10.00,
    "realAmount": 40.00,
    "bankCardId": 1,
    "status": "pending",
    "createTime": "2024-01-01T10:00:00"
  }
}
```

#### 获取提现记录
```
GET /api/withdrawals?page=0&size=20
Authorization: Bearer {token}
```

### 银行卡接口

#### 获取银行卡列表
```
GET /api/bank-cards
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "cardNumber": "6228 **** **** 8888",
      "bankName": "招商银行",
      "cardHolder": "张三",
      "type": "储蓄卡",
      "isDefault": true
    }
  ]
}
```

#### 添加银行卡
```
POST /api/bank-cards
Content-Type: application/json
Authorization: Bearer {token}

{
  "cardNumber": "6228888888888888",
  "bankName": "招商银行",
  "cardHolder": "张三",
  "phone": "13800138000"
}
```

## 开发步骤

### 1. 后端启动

确保后端服务正常运行：

```bash
cd /path/to/pengdeman
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8080`

### 2. 小程序配置

检查 `utils/api.js` 中的 `BASE_URL` 是否正确。

如果使用真机调试，需要：
- 使用局域网IP地址：`http://192.168.x.x:8080`
- 在微信开发者工具中勾选"不校验合法域名、web-view（业务域名）、TLS版本以及HTTPS证书"

### 3. 微信小程序配置

在 `project.config.json` 中配置正确的AppID：

```json
{
  "appid": "your_miniprogram_appid"
}
```

### 4. 后端配置

在 `application.yml` 中配置微信小程序的AppID和AppSecret：

```yaml
wechat:
  miniapp:
    app-id: your_app_id
    app-secret: your_app_secret
```

## 测试流程

1. **登录测试**
   - 打开小程序，点击允许获取用户信息
   - 检查是否成功登录并获取token

2. **商品查询测试**
   - 输入京东商品链接
   - 点击查询，检查是否返回商品信息

3. **订单创建测试**
   - 进入商品详情页
   - 点击下单，检查订单是否创建成功

4. **资金查询测试**
   - 进入资金管理页
   - 检查余额等信息是否正确

5. **提现申请测试**
   - 进入提现页
   - 输入金额，提交申请

## 常见问题

### 1. 网络请求失败

**原因**: 后端服务未启动或URL配置错误

**解决**:
- 确认后端服务运行在正确端口
- 检查 `BASE_URL` 配置
- 开发环境勾选"不校验合法域名"

### 2. 登录失败

**原因**: 微信AppID或AppSecret配置错误

**解决**:
- 检查后端 `application.yml` 中的微信配置
- 确认小程序AppID与后端配置一致
- 检查微信小程序后台的服务器域名配置

### 3. Token过期

**现象**: 请求返回401或提示登录过期

**解决**: `app.js` 中的 `handleError` 方法已自动处理此情况，会提示用户重新登录

### 4. 跨域问题

**原因**: 浏览器跨域限制

**解决**: 后端已配置CORS，确认 `WebConfig.java` 中的跨域配置正确

## 部署建议

### 生产环境部署

1. **后端部署**
   - 使用HTTPS协议
   - 配置正确的CORS域名
   - 使用生产环境数据库

2. **小程序部署**
   - 修改 `BASE_URL` 为生产环境地址
   - 在微信公众平台配置合法域名
   - 提交审核并发布

3. **安全建议**
   - 使用HTTPS
   - Token有效期设置合理
   - 敏感操作添加二次验证
   - 定期更新依赖包

## 下一步工作

1. **京东联盟API集成**
   - 后端集成真实的京东联盟API
   - 实现真实的商品查询和推广链接生成

2. **支付功能**
   - 集成微信支付
   - 实现订单支付流程

3. **更多页面**
   - 订单详情页
   - 提现记录页
   - 银行卡管理页
   - 个人设置页

4. **优化完善**
   - 添加加载状态
   - 优化错误提示
   - 添加下拉刷新和上拉加载
   - 实现图片懒加载

## 联系方式

如有问题，请联系开发团队。
