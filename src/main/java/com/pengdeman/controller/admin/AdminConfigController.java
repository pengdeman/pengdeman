package com.pengdeman.controller.admin;

import com.pengdeman.model.SystemConfigEntity;
import com.pengdeman.service.SystemConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台系统配置管理控制器
 */
@RestController
@RequestMapping("/api/admin/config")
public class AdminConfigController {

    private final SystemConfigService systemConfigService;

    public AdminConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    /**
     * 获取所有系统配置
     */
    @GetMapping
    public ResponseEntity<List<SystemConfigEntity>> getAll() {
        List<SystemConfigEntity> configs = systemConfigService.findAll();
        return ResponseEntity.ok(configs);
    }

    /**
     * 更新系统配置
     */
    @PostMapping
    public ResponseEntity<SystemConfigEntity> updateConfig(
            @RequestBody SystemConfigEntity request) {
        SystemConfigEntity saved = systemConfigService.saveConfig(
                request.getConfigKey(),
                request.getConfigValue(),
                request.getConfigDesc()
        );
        return ResponseEntity.ok(saved);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        systemConfigService.deleteConfig(id);
        return ResponseEntity.ok().build();
    }
}
