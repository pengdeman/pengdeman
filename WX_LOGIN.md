
# 微信小程序登录接口使用说明

## 概述

本项目已实现微信小程序登录功能，支持通过微信授权获取用户信息并生成JWT token用于后续接口认证。

## 前置准备

### 1. 获取微信小程序配置

登录 [微信公众平台](https://mp.weixin.qq.com/) 获取：
- **AppID**：小程序唯一凭证
- **AppSecret**：小程序唯一凭证密钥

### 2. 配置项目

修改 `src/main/resources/application.properties`：

```properties
# 微信小程序配置
wechat.miniapp.app-id=你的AppID
wechat.miniapp.app-secret=你的AppSecret
```

## 后端接口

### 微信登录接口

**接口地址：** `POST /api/auth/wx-login`

**请求参数：**
```json
{
  "code": "微信登录code",
  "nickname": "用户昵称（可选）",
  "avatar": "头像URL（可选）",
  "gender": 0
}
```

**响应数据：**
```json
{
  "token": "JWT认证token",
  "userId": 1,
  "openid": "用户openid",
  "nickname": "用户昵称",
  "avatar": "头像URL",
  "isNewUser": true
}
```

## 小程序端代码示例

### 方式一：简单登录（仅获取openid）

```javascript
// app.js
App({
  onLaunch() {
    this.wxLogin();
  },

  wxLogin() {
    // 1. 调用微信登录获取code
    wx.login({
      success: (res) => {
        if (res.code) {
          // 2. 发送code到后端
          this.requestLogin(res.code);
        } else {
          console.log('登录失败：' + res.errMsg);
        }
      }
    });
  },

  requestLogin(code) {
    wx.request({
      url: 'http://localhost:8080/api/auth/wx-login',
      method: 'POST',
      data: {
        code: code
      },
      success: (res) => {
        console.log('登录成功', res.data);
        // 保存token和用户信息
        wx.setStorageSync('token', res.data.token);
        wx.setStorageSync('userInfo', res.data);
      },
      fail: (err) => {
        console.error('登录请求失败', err);
      }
    });
  }
});
```

### 方式二：完整登录（获取用户信息）

```javascript
// pages/login/login.js
Page({
  data: {
    userInfo: null,
    hasLogin: false
  },

  onLoad() {
    // 检查是否已登录
    const token = wx.getStorageSync('token');
    if (token) {
      this.setData({ hasLogin: true });
    }
  },

  // 获取用户信息（新的版本需要用户主动点击按钮）
  getUserProfile() {
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        this.setData({
          userInfo: res.userInfo,
          hasLogin: true
        });
        this.loginWithUserInfo(res.userInfo);
      }
    });
  },

  loginWithUserInfo(userInfo) {
    wx.login({
      success: (res) => {
        if (res.code) {
          this.requestWxLogin(res.code, userInfo);
        }
      }
    });
  },

  requestWxLogin(code, userInfo) {
    wx.showLoading({ title: '登录中...' });

    wx.request({
      url: 'http://localhost:8080/api/auth/wx-login',
      method: 'POST',
      data: {
        code: code,
        nickname: userInfo.nickName,
        avatar: userInfo.avatarUrl,
        gender: userInfo.gender
      },
      success: (res) => {
        console.log('登录成功', res.data);
        wx.hideLoading();

        // 保存登录状态
        wx.setStorageSync('token', res.data.token);
        wx.setStorageSync('userId', res.data.userId);
        wx.setStorageSync('openid', res.data.openid);

        // 提示用户
        if (res.data.isNewUser) {
          wx.showToast({ title: '注册成功' });
        } else {
          wx.showToast({ title: '登录成功' });
        }

        // 跳转到首页
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' });
        }, 1500);
      },
      fail: (err) => {
        wx.hideLoading();
        wx.showToast({
          title: '登录失败',
          icon: 'none'
        });
      }
    });
  }
});
```

### 方式三：最新版小程序（使用头像昵称填写能力）

```html
<!-- pages/login/login.wxml -->
<view class="container">
  <view class="header">
    <button class="avatar-wrapper" open-type="chooseAvatar" bind:chooseavatar="onChooseAvatar">
      <image class="avatar" src="{{avatarUrl || '/images/default-avatar.png'}}"></image>
    </button>
    <view class="nickname-input">
      <input type="nickname" class="weui-input" placeholder="请输入昵称" bindblur="onNicknameInput"/>
    </view>
  </view>

  <button class="login-btn" bindtap="doLogin" disabled="{{!avatarUrl || !nickname}}">
    登录
  </button>
</view>
```

```javascript
// pages/login/login.js
Page({
  data: {
    avatarUrl: '',
    nickname: ''
  },

  onChooseAvatar(e) {
    this.setData({
      avatarUrl: e.detail.avatarUrl
    });
  },

  onNicknameInput(e) {
    this.setData({
      nickname: e.detail.value
    });
  },

  doLogin() {
    wx.login({
      success: (res) => {
        if (res.code) {
          wx.request({
            url: 'http://localhost:8080/api/auth/wx-login',
            method: 'POST',
            data: {
              code: res.code,
              nickname: this.data.nickname,
              avatar: this.data.avatarUrl
            },
            success: (loginRes) => {
              wx.setStorageSync('token', loginRes.data.token);
              wx.showToast({ title: '登录成功' });
            }
          });
        }
      }
    });
  }
});
```

## 使用Token进行后续请求

在后续的API请求中，需要在请求头中携带JWT token：

```javascript
// 封装的请求方法
function requestWithAuth(url, method = 'GET', data = {}) {
  const token = wx.getStorageSync('token');

  return new Promise((resolve, reject) => {
    wx.request({
      url: url,
      method: method,
      data: data,
      header: {
        'Authorization': 'Bearer ' + token,
        'Content-Type': 'application/json'
      },
      success: (res) => {
        resolve(res.data);
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
}

// 使用示例
requestWithAuth('http://localhost:8080/api/demo')
  .then(data => {
    console.log(data);
  });
```

## 接口测试

### 使用curl测试

```bash
# 注意：需要真实的微信code才能测试
curl -X POST http://localhost:8080/api/auth/wx-login \
  -H "Content-Type: application/json" \
  -d '{
    "code": "微信登录code",
    "nickname": "测试用户",
    "avatar": "https://example.com/avatar.jpg"
  }'
```

## 数据库表结构

用户表 `users` 会自动创建，包含以下字段：

| 字段 | 说明 |
|------|------|
| id | 主键 |
| openid | 微信openid（唯一） |
| unionid | 微信unionid |
| nickname | 昵称 |
| avatar | 头像URL |
| gender | 性别 |
| country | 国家 |
| province | 省份 |
| city | 城市 |
| status | 状态 |
| last_login_at | 最后登录时间 |
| created_at | 创建时间 |
| updated_at | 更新时间 |

## 安全建议

1. **生产环境配置**
   - 使用环境变量存储AppSecret，不要提交到代码仓库
   - JWT密钥使用强随机字符串
   - 配置HTTPS

2. **后续优化**
   - 添加token刷新机制
   - 实现token黑名单（用户注销时）
   - 添加接口权限控制
   - 实现用户信息加密存储

## 常见问题

### 1. code无效错误（errcode: 40029）
- 确保code是新获取的（code只能使用一次）
- 确保AppID和AppSecret配置正确

### 2. 请求频率限制（errcode: 45011）
- 微信API有频率限制，请避免短时间内频繁请求
- 建议前端做防抖处理

### 3. unionid为空
- 需要在微信开放平台绑定小程序
- 只有用户关注过同主体的公众号或使用过同主体的其他应用才会有unionid