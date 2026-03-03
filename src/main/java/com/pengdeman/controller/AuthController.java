package com.pengdeman.controller;

import com.pengdeman.dto.WxLoginRequest;
import com.pengdeman.dto.WxLoginResponse;
import com.pengdeman.model.UserEntity;
import com.pengdeman.service.WeChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器 - 处理微信小程序登录等认证相关接口
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final WeChatService weChatService;

    public AuthController(WeChatService weChatService) {
        this.weChatService = weChatService;
    }

    /**
     * 微信小程序登录接口
     *
     * POST /api/auth/wx-login
     *
     * 小程序端调用流程：
     * 1. 调用 wx.login() 获取 code
     * 2. 调用 wx.getUserProfile() 获取用户信息（可选）
     * 3. 将 code 和用户信息发送到此接口
     *
     * @param request 登录请求
     * @return 登录响应，包含token和用户信息
     */
    @PostMapping("/wx-login")
    public ResponseEntity<WxLoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        WxLoginResponse response = weChatService.wxLogin(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前登录用户信息
     *
     * GET /api/auth/user/{userId}
     *
     * 注：实际使用时应该从token中解析userId，而不是从路径参数获取
     * 这里为了演示方便，暂时使用路径参数
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserEntity> getUserInfo(@PathVariable Long userId) {
        UserEntity user = weChatService.getUserById(userId);
        // 不返回敏感信息
        user.setOpenid(null);
        user.setUnionid(null);
        return ResponseEntity.ok(user);
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running！");
    }
}