package com.pengdeman.service;

import com.pengdeman.dto.UserFinanceDTO;
import com.pengdeman.exception.UserFinanceException;
import com.pengdeman.model.UserFinanceEntity;
import com.pengdeman.repository.UserFinanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户资金管理服务
 */
@Service
@Transactional
public class UserFinanceService {

    private final UserFinanceRepository userFinanceRepository;

    public UserFinanceService(UserFinanceRepository userFinanceRepository) {
        this.userFinanceRepository = userFinanceRepository;
    }

    /**
     * 获取用户资金信息
     */
    @Transactional(readOnly = true)
    public UserFinanceDTO getUserFinance(Long userId) {
        UserFinanceEntity finance = userFinanceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultUserFinance(userId));

        return convertToDTO(finance);
    }

    /**
     * 创建默认用户资金信息
     */
    private UserFinanceEntity createDefaultUserFinance(Long userId) {
        UserFinanceEntity finance = new UserFinanceEntity();
        finance.setUserId(userId);
        finance.setBalance(BigDecimal.ZERO);
        finance.setTotalIncome(BigDecimal.ZERO);
        finance.setWithdrawableAmount(BigDecimal.ZERO);
        finance.setOrderCount(0);
        finance.setPendingWithdrawal(BigDecimal.ZERO);
        finance.setTotalWithdrawn(BigDecimal.ZERO);

        return userFinanceRepository.save(finance);
    }

    /**
     * 获取用户可提现金额
     */
    @Transactional(readOnly = true)
    public BigDecimal getWithdrawableAmount(Long userId) {
        UserFinanceEntity finance = userFinanceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserFinanceException("用户资金信息不存在"));

        return finance.getWithdrawableAmount();
    }

    /**
     * 增加用户余额
     */
    public void addBalance(Long userId, BigDecimal amount) {
        UserFinanceEntity finance = userFinanceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultUserFinance(userId));

        finance.setBalance(finance.getBalance().add(amount));
        finance.setTotalIncome(finance.getTotalIncome().add(amount));
        // 可提现金额为余额的80%
        finance.setWithdrawableAmount(finance.getBalance().multiply(BigDecimal.valueOf(0.8)).setScale(2, BigDecimal.ROUND_HALF_UP));

        userFinanceRepository.save(finance);
    }

    /**
     * 减少用户余额（用于提现）
     */
    public void deductBalance(Long userId, BigDecimal amount) {
        UserFinanceEntity finance = userFinanceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserFinanceException("用户资金信息不存在"));

        if (finance.getWithdrawableAmount().compareTo(amount) < 0) {
            throw new UserFinanceException("可提现金额不足");
        }

        finance.setBalance(finance.getBalance().subtract(amount));
        finance.setWithdrawableAmount(finance.getWithdrawableAmount().subtract(amount));
        finance.setPendingWithdrawal(finance.getPendingWithdrawal().add(amount));

        userFinanceRepository.save(finance);
    }

    /**
     * 完成提现
     */
    public void completeWithdrawal(Long userId, BigDecimal amount) {
        UserFinanceEntity finance = userFinanceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserFinanceException("用户资金信息不存在"));

        finance.setPendingWithdrawal(finance.getPendingWithdrawal().subtract(amount));
        finance.setTotalWithdrawn(finance.getTotalWithdrawn().add(amount));

        userFinanceRepository.save(finance);
    }

    /**
     * 转换UserFinanceEntity到UserFinanceDTO
     */
    private UserFinanceDTO convertToDTO(UserFinanceEntity entity) {
        UserFinanceDTO dto = new UserFinanceDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setBalance(entity.getBalance());
        dto.setTotalIncome(entity.getTotalIncome());
        dto.setWithdrawableAmount(entity.getWithdrawableAmount());
        dto.setOrderCount(entity.getOrderCount());
        dto.setPendingWithdrawal(entity.getPendingWithdrawal());
        dto.setTotalWithdrawn(entity.getTotalWithdrawn());

        return dto;
    }
}
