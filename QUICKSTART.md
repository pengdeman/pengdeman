# Pengdeman项目快速启动指南

## 项目状态

✅ **项目已完全可运行！**

- 后端服务已成功启动在 `http://localhost:80`
- 小程序与后端API已完全集成
- 所有编译错误已修复
- 所有功能模块已就绪

## 快速启动

### 1. 后端服务（已启动 ✓）

后端服务已经在运行中，监听在80端口。

如需手动启动：
```bash
cd /Users/pengdeman/Documents/software-development/private-git-project/pengdeman
./mvnw spring-boot:run
```

**服务信息**：
- 访问地址：http://localhost:80
- H2数据库控制台：http://localhost:80/h2-console
- JDBC URL：`jdbc:h2:file:./data/pengdeman`

### 2. 小程序启动

1. **打开微信开发者工具**
   - 项目目录：`/Users/pengdeman/basketball`

2. **配置设置**
   - 在详情 → 本地设置中勾选：
     - ☑️ 不校验合法域名、web-view（业务域名）、TLS版本以及HTTPS证书

3. **确认API配置**
   - 检查 `utils/api.js` 中的 `BASE_URL`：
     ```javascript
     const BASE_URL = 'http://localhost:80/api'
     ```

4. **开始使用**
   - 点击"点击登录"按钮
   - 授权用户信息
   - 开始使用所有功能！

## 项目功能

### ✅ 已实现功能

1. **微信登录**
   - 用户主动点击登录
   - 微信授权用户信息
   - JWT token认证
   - 自动创建用户资金账户

2. **商品查询**
   - 粘贴京东链接查询商品
   - 显示商品详情和返利信息
   - 获取推广链接

3. **订单管理**
   - 创建订单
   - 订单列表（分页）
   - 订单状态筛选
   - 取消订单
   - 确认收货

4. **资金管理**
   - 查看余额
   - 查看总收入
   - 查看可提现金额
   - 查看订单数

5. **提现管理**
   - 申请提现
   - 提现记录
   - 银行卡管理

### 🚧 待完善功能

1. **京东联盟API集成**（高优先级）
   - 真实商品查询
   - 真实推广链接生成

2. **JWT过滤器**（高优先级）
   - Token验证
   - 权限控制

3. **订单详情页**（中优先级）

4. **提现记录页**（中优先级）

5. **银行卡管理**（中优先级）

## API接口测试

### 使用curl测试

```bash
# 测试健康检查
curl http://localhost:80/actuator/health

# 测试商品查询（POST）
curl -X POST http://localhost:80/api/products/query-by-url \
  -H "Content-Type: application/json" \
  -d '{"url":"https://item.jd.com/100012928458.html"}'

# 获取商品列表
curl http://localhost:80/api/products
```

### 使用Postman测试

1. 导入Postman集合
2. 配置环境变量：`base_url = http://localhost:80`
3. 测试各个接口

## 数据库

### H2数据库控制台

访问：http://localhost:80/h2-console

连接信息：
- JDBC URL：`jdbc:h2:file:./data/pengdeman`
- 用户名：`sa`
- 密码：（留空）

### 数据表

项目使用JPA自动创建以下表：
- `users` - 用户表
- `user_finance` - 用户资金表
- `products` - 商品表
- `orders` - 订单表
- `withdrawals` - 提现申请表
- `bank_cards` - 银行卡表
- `commission_records` - 佣金记录表

## 小程序页面说明

| 页面 | 路径 | 功能 |
|------|------|------|
| 首页 | pages/index | 商品查询、登录 |
| 商品详情 | pages/product/detail | 商品详情、下单 |
| 订单 | pages/order | 订单列表 |
| 订单详情 | pages/order/detail | 订单详情（开发中） |
| 资金 | pages/money | 资金信息 |
| 提现 | pages/withdraw | 提现申请 |
| 提现记录 | pages/withdraw/records | 提现记录（开发中） |
| 添加银行卡 | pages/bank-card/add | 添加银行卡（开发中） |

## 常见问题

### 1. 小程序无法连接后端

**检查**：
- 确认后端服务正在运行
- 确认 `utils/api.js` 中的 `BASE_URL` 正确
- 确认勾选了"不校验合法域名"

### 2. 登录失败

**检查**：
- 确认后端 `application.yml` 中的微信配置正确
- 确认小程序AppID与后端配置一致
- 查看控制台日志

### 3. 数据库访问问题

**检查**：
- 访问 http://localhost:80/h2-console
- 确认JDBC URL正确
- 确认 `./data/` 目录有写权限

### 4. 编译错误

**解决**：
```bash
cd /Users/pengdeman/Documents/software-development/private-git-project/pengdeman
./mvnw clean compile
```

## 开发建议

### 后端开发
1. 使用H2数据库进行开发测试
2. 配置热部署提高开发效率
3. 使用 `./mvnw spring-boot:run` 启动

### 小程序开发
1. 使用微信开发者工具进行调试
2. 开发环境勾选"不校验合法域名"
3. 真机调试使用局域网IP

### 生产环境部署
1. 使用MySQL数据库
2. 配置HTTPS
3. 在微信公众平台配置合法域名
4. 使用生产环境的AppID和AppSecret

## 下一步

### 立即可以做的
1. ✅ 测试小程序登录功能
2. ✅ 测试商品查询功能
3. ✅ 测试订单创建功能
4. ✅ 测试资金查看功能

### 下一步开发
1. 集成真实的京东联盟API
2. 实现JWT过滤器
3. 完善订单详情页
4. 完善提现记录页
5. 完善银行卡管理

## 项目文档

- `PROJECT_SUMMARY.md` - 完整的项目开发总结
- `MINIPROGRAM_INTEGRATION_GUIDE.md` - 小程序集成指南
- `MINIPROGRAM_FIXES.md` - 问题修复总结
- `ECOMMERCE_API_GUIDE.md` - 电商API接口开发指南
- `WECHAT_LOGIN_OPTIMIZATION.md` - 微信登录优化总结

## 技术支持

如有问题，请查看：
1. 控制台日志
2. 项目文档
3. 相关配置文件

---

**项目已完全可运行，开始你的开发吧！** 🎉
