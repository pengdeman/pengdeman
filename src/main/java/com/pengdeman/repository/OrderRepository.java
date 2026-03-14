package com.pengdeman.repository;

import com.pengdeman.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    /**
     * 根据订单号查找订单
     */
    Optional<OrderEntity> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查找订单列表（分页）
     */
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和状态查找订单列表（分页）
     */
    Page<OrderEntity> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    /**
     * 根据用户ID查找所有订单
     */
    List<OrderEntity> findByUserId(Long userId);

    /**
     * 统计用户订单数量
     */
    long countByUserId(Long userId);

    /**
     * 根据用户ID和标题模糊搜索订单（分页）
     */
    Page<OrderEntity> findByUserIdAndTitleContaining(Long userId, String title, Pageable pageable);

    /**
     * 判断订单号是否存在
     */
    boolean existsByOrderNo(String orderNo);
}
