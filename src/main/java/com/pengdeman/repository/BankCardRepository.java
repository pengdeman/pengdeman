package com.pengdeman.repository;

import com.pengdeman.model.BankCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 银行卡数据访问接口
 */
@Repository
public interface BankCardRepository extends JpaRepository<BankCardEntity, Long> {

    /**
     * 根据用户ID查找银行卡列表
     */
    List<BankCardEntity> findByUserId(Long userId);

    /**
     * 根据用户ID和是否默认查找银行卡
     */
    Optional<BankCardEntity> findByUserIdAndIsDefault(Long userId, Integer isDefault);

    /**
     * 根据用户ID和银行卡号查找银行卡
     */
    Optional<BankCardEntity> findByUserIdAndCardNumber(Long userId, String cardNumber);

    /**
     * 统计用户银行卡数量
     */
    long countByUserId(Long userId);

    /**
     * 判断用户是否已添加该银行卡
     */
    boolean existsByUserIdAndCardNumber(Long userId, String cardNumber);
}
