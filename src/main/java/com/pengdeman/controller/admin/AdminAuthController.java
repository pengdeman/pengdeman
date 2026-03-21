package com.pengdeman.controller.admin;

import com.pengdeman.dto.admin.AdminLoginRequest;
import com.pengdeman.dto.admin.AdminLoginResponse;
import com.pengdeman.service.AdminAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 管理员认证控制器
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        Optional<AdminLoginResponse> result = adminAuthService.login(request);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(401).body("用户名或密码错误");
        }
    }
}
