package com.pengdeman.controller.admin;

import com.pengdeman.dto.PageResponse;
import com.pengdeman.model.UserEntity;
import com.pengdeman.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageResponse<UserEntity>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserEntity> pageResult = userRepository.findAll(pageable);

        List<UserEntity> content = new ArrayList<>(pageResult.getContent());

        PageResponse<UserEntity> response = new PageResponse<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> detail(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 切换用户禁用/启用状态
     */
    @PostMapping("/{id}/toggle-enabled")
    public ResponseEntity<UserEntity> toggleEnabled(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    // status: 0-禁用 1-正常
                    user.setStatus(user.getStatus() == 1 ? 0 : 1);
                    UserEntity saved = userRepository.save(user);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
