package com.pengdeman.controller;

import com.pengdeman.dto.WxLoginResponse;
import com.pengdeman.model.UserEntity;
import com.pengdeman.repository.UserRepository;
import com.pengdeman.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测试控制器 - 用于开发测试
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public TestController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 模拟微信登录 - 用于测试（不需要真实微信code）
     *
     * POST /api/test/mock-login
     */
    @PostMapping("/mock-login")
    public ResponseEntity<WxLoginResponse> mockLogin(@RequestBody Map<String, String> params) {
        String mockOpenid = params.getOrDefault("openid", "mock_openid_" + System.currentTimeMillis());
        String nickname = params.getOrDefault("nickname", "测试用户");
        String avatar = params.getOrDefault("avatar", "https://example.com/avatar.jpg");

        // 查找或创建用户
        Optional<UserEntity> existingUser = userRepository.findByOpenid(mockOpenid);
        UserEntity user;
        boolean isNewUser = false;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setLastLoginAt(LocalDateTime.now());
        } else {
            user = new UserEntity();
            user.setOpenid(mockOpenid);
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setStatus(1);
            user.setLastLoginAt(LocalDateTime.now());
            isNewUser = true;
        }

        user = userRepository.save(user);

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getOpenid());

        // 构建响应
        WxLoginResponse response = WxLoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .openid(user.getOpenid())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .isNewUser(isNewUser)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有用户列表 - 测试用
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        // 隐藏敏感信息
        for (UserEntity user : users) {
            user.setOpenid(null);
            user.setUnionid(null);
        }
        return ResponseEntity.ok(users);
    }

    /**
     * 验证token - 测试用
     */
    @PostMapping("/verify-token")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody Map<String, String> params) {
        String token = params.get("token");
        Map<String, Object> result = new HashMap<>();

        if (token == null || token.isEmpty()) {
            result.put("valid", false);
            result.put("message", "token不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        boolean valid = jwtUtil.validateToken(token);
        result.put("valid", valid);

        if (valid) {
            result.put("userId", jwtUtil.getUserIdFromToken(token));
            result.put("openid", jwtUtil.getOpenidFromToken(token));
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 测试接口总览
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> testInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("message", "测试接口可用");
        info.put("endpoints", new String[]{
                "POST /api/test/mock-login - 模拟微信登录",
                "GET /api/test/users - 获取所有用户",
                "POST /api/test/verify-token - 验证token"
        });
        return ResponseEntity.ok(info);
    }
}