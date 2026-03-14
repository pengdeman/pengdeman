package com.pengdeman.repository;

import com.pengdeman.model.UserFinanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户资金数据访问接口
 */
@Repository
public interface UserFinanceRepository extends JpaRepository<UserFinanceEntity, Long> {

    /**
     * 根据用户ID查找资金信息
     */
    Optional<UserFinanceEntity> findByUserId(Long userId);

    /**
     * 判断用户是否已存在资金信息
     */
    boolean existsByUserId(Long userId);
}
