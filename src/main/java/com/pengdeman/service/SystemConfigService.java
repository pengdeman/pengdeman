package com.pengdeman.service;

import com.pengdeman.model.SystemConfigEntity;
import com.pengdeman.repository.SystemConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置服务
 * 管理平台可配置项，提供内存缓存加速读取
 */
@Service
public class SystemConfigService {

    private static final Logger log = LoggerFactory.getLogger(SystemConfigService.class);

    private final SystemConfigRepository systemConfigRepository;

    // 内存缓存
    private final ConcurrentHashMap<String, String> configCache = new ConcurrentHashMap<>();

    public SystemConfigService(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    /**
     * 系统启动时初始化缓存和默认配置
     */
    @PostConstruct
    public void init() {
        // 加载所有配置到缓存
        List<SystemConfigEntity> allConfigs = systemConfigRepository.findAll();
        for (SystemConfigEntity config : allConfigs) {
            configCache.put(config.getConfigKey(), config.getConfigValue());
        }

        // 初始化默认配置
        initDefaultConfigIfNotExists("min_withdrawal_amount", "10.00", "最低提现金额");
        initDefaultConfigIfNotExists("user_rebate_rate", "20.00", "用户返利比例(%)");
        initDefaultConfigIfNotExists("site_enabled", "true", "站点是否启用");
        initDefaultConfigIfNotExists("announcement", "", "系统公告");

        log.info("SystemConfig initialized, {} configs loaded", configCache.size());
    }

    /**
     * 获取所有配置
     */
    public List<SystemConfigEntity> findAll() {
        return systemConfigRepository.findAll();
    }

    /**
     * 根据key获取配置
     */
    public Optional<SystemConfigEntity> findByKey(String key) {
        return systemConfigRepository.findByConfigKey(key);
    }

    /**
     * 获取配置值（从缓存）
     */
    public String getConfigValue(String key) {
        return configCache.get(key);
    }

    /**
     * 获取配置值，默认值
     */
    public String getConfigValue(String key, String defaultValue) {
        return configCache.getOrDefault(key, defaultValue);
    }

    /**
     * 获取BigDecimal类型配置
     */
    public BigDecimal getBigDecimalConfig(String key, BigDecimal defaultValue) {
        String value = configCache.get(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取Boolean类型配置
     */
    public Boolean getBooleanConfig(String key, Boolean defaultValue) {
        String value = configCache.get(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    /**
     * 更新或新增配置
     */
    @Transactional
    public SystemConfigEntity saveConfig(String key, String value, String description) {
        SystemConfigEntity config = systemConfigRepository.findByConfigKey(key)
                .orElse(new SystemConfigEntity());

        config.setConfigKey(key);
        config.setConfigValue(value);
        if (description != null) {
            config.setConfigDesc(description);
        }

        SystemConfigEntity saved = systemConfigRepository.save(config);

        // 更新缓存
        configCache.put(key, value);

        log.info("Config updated: key={}, value={}", key, value);

        return saved;
    }

    /**
     * 删除配置
     */
    @Transactional
    public void deleteConfig(Long id) {
        SystemConfigEntity config = systemConfigRepository.findById(id).orElse(null);
        if (config != null) {
            systemConfigRepository.deleteById(id);
            configCache.remove(config.getConfigKey());
            log.info("Config deleted: id={}, key={}", id, config.getConfigKey());
        }
    }

    /**
     * 获取最低提现金额
     */
    public BigDecimal getMinWithdrawalAmount() {
        return getBigDecimalConfig("min_withdrawal_amount", new BigDecimal("10.00"));
    }

    /**
     * 获取用户返利比例（百分比）
     */
    public BigDecimal getUserRebateRate() {
        return getBigDecimalConfig("user_rebate_rate", new BigDecimal("20.00"));
    }

    /**
     * 初始化默认配置（如果不存在）
     */
    private void initDefaultConfigIfNotExists(String key, String defaultValue, String desc) {
        if (!systemConfigRepository.existsByConfigKey(key)) {
            saveConfig(key, defaultValue, desc);
            log.info("Initialized default config: {} = {}", key, defaultValue);
        }
    }
}
