package com.pengdeman.service;

import com.pengdeman.config.AdminConfig;
import com.pengdeman.dto.admin.AdminLoginRequest;
import com.pengdeman.dto.admin.AdminLoginResponse;
import com.pengdeman.model.UserEntity;
import com.pengdeman.repository.UserRepository;
import com.pengdeman.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * 管理员认证服务
 * 处理管理员登录、初始化默认管理员
 */
@Service
public class AdminAuthService {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthService.class);

    private final UserRepository userRepository;
    private final AdminConfig adminConfig;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminAuthService(UserRepository userRepository,
                            AdminConfig adminConfig,
                            JwtUtil jwtUtil,
                            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminConfig = adminConfig;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 系统启动后初始化默认管理员
     */
    @PostConstruct
    public void initDefaultAdmin() {
        // 检查是否已有管理员
        boolean hasAdmin = userRepository.findAll().stream()
                .anyMatch(UserEntity::getIsAdmin);

        if (!hasAdmin) {
            String username = adminConfig.getUsername();
            String password = adminConfig.getPassword();

            UserEntity admin = new UserEntity();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setIsAdmin(true);
            admin.setStatus(1);
            admin.setNickname("系统管理员");

            userRepository.save(admin);

            log.info("Default admin initialized: username={}", username);
        }
    }

    /**
     * 管理员登录
     */
    public Optional<AdminLoginResponse> login(AdminLoginRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(request.getUsername());

        if (!userOpt.isPresent()) {
            log.info("Admin login failed: username {} not found", request.getUsername());
            return Optional.empty();
        }

        UserEntity user = userOpt.get();

        if (!user.getIsAdmin()) {
            log.info("Admin login failed: user {} is not admin", request.getUsername());
            return Optional.empty();
        }

        if (user.getStatus() != 1) {
            log.info("Admin login failed: user {} is disabled", request.getUsername());
            return Optional.empty();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.info("Admin login failed: password incorrect for {}", request.getUsername());
            return Optional.empty();
        }

        // 生成JWT Token - 管理员用户名传null给openid
        String token = jwtUtil.generateToken(user.getId(), null);

        AdminLoginResponse response = AdminLoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .isAdmin(user.getIsAdmin())
                .build();

        log.info("Admin login successful: username={}", request.getUsername());

        return Optional.of(response);
    }

    /**
     * 验证用户是否是管理员
     */
    public boolean isAdmin(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        return userOpt.isPresent() && Boolean.TRUE.equals(userOpt.get().getIsAdmin())
                && userOpt.get().getStatus() == 1;
    }
}
