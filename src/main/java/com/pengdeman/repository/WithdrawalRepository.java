package com.pengdeman.repository;

import com.pengdeman.model.WithdrawalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 提现申请数据访问接口
 */
@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, Long> {

    /**
     * 根据用户ID查找提现记录（分页）
     */
    Page<WithdrawalEntity> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID查找所有提现记录
     */
    List<WithdrawalEntity> findByUserId(Long userId);

    /**
     * 根据用户ID和状态查找提现记录（分页）
     */
    Page<WithdrawalEntity> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    /**
     * 统计用户提现记录数量
     */
    long countByUserId(Long userId);

    /**
     * 统计用户待审核提现数量
     */
    long countByUserIdAndStatus(Long userId, Integer status);
}
