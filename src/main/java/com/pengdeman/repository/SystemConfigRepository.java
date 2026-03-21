package com.pengdeman.repository;

import com.pengdeman.model.SystemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 系统配置数据访问接口
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfigEntity, Long> {

    /**
     * 根据配置键查找
     */
    Optional<SystemConfigEntity> findByConfigKey(String configKey);

    /**
     * 判断配置键是否存在
     */
    boolean existsByConfigKey(String configKey);
}
