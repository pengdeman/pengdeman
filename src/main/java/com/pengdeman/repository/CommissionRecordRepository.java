package com.pengdeman.repository;

import com.pengdeman.model.CommissionRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 佣金记录数据访问接口
 */
@Repository
public interface CommissionRecordRepository extends JpaRepository<CommissionRecordEntity, Long> {

    /**
     * 根据用户ID查找佣金记录（分页）
     */
    Page<CommissionRecordEntity> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和状态查找佣金记录（分页）
     */
    Page<CommissionRecordEntity> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    /**
     * 根据用户ID查找所有佣金记录
     */
    List<CommissionRecordEntity> findByUserId(Long userId);

    /**
     * 根据订单ID查找佣金记录
     */
    List<CommissionRecordEntity> findByOrderId(Long orderId);

    /**
     * 统计用户佣金记录数量
     */
    long countByUserId(Long userId);

    /**
     * 统计用户待结算佣金数量
     */
    long countByUserIdAndStatus(Long userId, Integer status);
}
