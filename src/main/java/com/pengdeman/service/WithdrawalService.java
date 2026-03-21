package com.pengdeman.service;

import com.pengdeman.dto.PageResponse;
import com.pengdeman.dto.WithdrawalDTO;
import com.pengdeman.dto.WithdrawalCreateRequest;
import com.pengdeman.dto.WithdrawalEligibilityResponse;
import com.pengdeman.exception.BankCardException;
import com.pengdeman.exception.UserFinanceException;
import com.pengdeman.exception.WithdrawalException;
import com.pengdeman.model.BankCardEntity;
import com.pengdeman.model.WithdrawalEntity;
import com.pengdeman.repository.BankCardRepository;
import com.pengdeman.repository.WithdrawalRepository;
import com.pengdeman.service.SystemConfigService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 提现管理服务
 */
@Service
@Transactional
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final BankCardRepository bankCardRepository;
    private final UserFinanceService userFinanceService;
    private final SystemConfigService systemConfigService;

    public WithdrawalService(WithdrawalRepository withdrawalRepository,
                             BankCardRepository bankCardRepository,
                             UserFinanceService userFinanceService,
                             SystemConfigService systemConfigService) {
        this.withdrawalRepository = withdrawalRepository;
        this.bankCardRepository = bankCardRepository;
        this.userFinanceService = userFinanceService;
        this.systemConfigService = systemConfigService;
    }

    /**
     * 检查用户是否可以申请提现
     */
    public WithdrawalEligibilityResponse checkEligibility(Long userId) {
        // 1. 检查本月是否已经申请过提现（除了已拒绝的）
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        LocalDateTime start = startOfMonth.atStartOfDay();
        LocalDateTime end = endOfMonth.atTime(23, 59, 59);

        // 状态 4=已拒绝，排除已拒绝的
        boolean hasApplied = withdrawalRepository.existsByUserIdAndCreatedAtBetweenAndStatusNot(
                userId, start, end, 4);

        if (hasApplied) {
            return WithdrawalEligibilityResponse.builder()
                    .canApply(false)
                    .reason("本月已申请过提现，每个自然月只能申请一次")
                    .maxAmount(BigDecimal.ZERO)
                    .build();
        }

        // 2. 获取可提现余额
        BigDecimal available = userFinanceService.getWithdrawableAmount(userId);
        BigDecimal minAmount = systemConfigService.getMinWithdrawalAmount();

        if (available.compareTo(minAmount) < 0) {
            return WithdrawalEligibilityResponse.builder()
                    .canApply(false)
                    .reason("可提现余额不足最低提现金额要求，最低提现金额为 " + minAmount + " 元")
                    .maxAmount(available)
                    .build();
        }

        return WithdrawalEligibilityResponse.builder()
                .canApply(true)
                .reason(null)
                .maxAmount(available)
                .minAmount(minAmount)
                .build();
    }

    /**
     * 创建提现申请
     */
    @Transactional
    public WithdrawalDTO createWithdrawal(Long userId, WithdrawalCreateRequest request) {
        // 1. 验证银行卡是否属于该用户
        BankCardEntity bankCard = bankCardRepository.findById(request.getBankCardId())
                .orElseThrow(() -> new BankCardException("银行卡不存在"));

        if (!bankCard.getUserId().equals(userId)) {
            throw new BankCardException("银行卡不属于该用户");
        }

        // 2. 验证提现金额是否可用
        if (userFinanceService.getWithdrawableAmount(userId).compareTo(request.getAmount()) < 0) {
            throw new UserFinanceException("可提现金额不足");
        }

        // 3. 创建提现申请
        WithdrawalEntity withdrawal = new WithdrawalEntity();
        withdrawal.setUserId(userId);
        withdrawal.setAmount(request.getAmount());
        withdrawal.setBankCardId(request.getBankCardId());
        withdrawal.setStatus(1); // 待审核

        WithdrawalEntity savedWithdrawal = withdrawalRepository.save(withdrawal);

        // 4. 减少用户余额和可提现金额
        userFinanceService.deductBalance(userId, request.getAmount());

        return convertToDTO(savedWithdrawal, bankCard);
    }

    /**
     * 获取用户提现记录（分页）
     */
    @Transactional(readOnly = true)
    public PageResponse<WithdrawalDTO> getUserWithdrawals(Long userId, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<WithdrawalEntity> withdrawalPage;

        if (status != null && status > 0) {
            withdrawalPage = withdrawalRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            withdrawalPage = withdrawalRepository.findByUserId(userId, pageable);
        }

        List<WithdrawalDTO> withdrawalDTOs = new ArrayList<>();
        for (WithdrawalEntity entity : withdrawalPage.getContent()) {
            BankCardEntity bankCard = bankCardRepository.findById(entity.getBankCardId())
                    .orElseThrow(() -> new BankCardException("银行卡信息不存在"));
            withdrawalDTOs.add(convertToDTO(entity, bankCard));
        }

        return new PageResponse<>(
                withdrawalDTOs,
                withdrawalPage.getNumber(),
                withdrawalPage.getSize(),
                withdrawalPage.getTotalElements(),
                withdrawalPage.getTotalPages()
        );
    }

    /**
     * 获取提现详情
     */
    @Transactional(readOnly = true)
    public WithdrawalDTO getWithdrawalById(Long userId, Long withdrawalId) {
        WithdrawalEntity entity = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new WithdrawalException("提现记录不存在"));

        if (!entity.getUserId().equals(userId)) {
            throw new WithdrawalException("提现记录不属于该用户");
        }

        BankCardEntity bankCard = bankCardRepository.findById(entity.getBankCardId())
                .orElseThrow(() -> new BankCardException("银行卡信息不存在"));

        return convertToDTO(entity, bankCard);
    }

    /**
     * 审核提现申请
     */
    @Transactional
    public WithdrawalDTO approveWithdrawal(Long withdrawalId, Boolean approved, String auditNote) {
        WithdrawalEntity entity = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new WithdrawalException("提现记录不存在"));

        // 只有待审核状态的提现申请可以审核
        if (entity.getStatus() != 1) {
            throw new WithdrawalException("提现申请状态不允许审核");
        }

        if (approved) {
            entity.setStatus(2); // 已批准
            // 这里可以添加打款逻辑
        } else {
            entity.setStatus(4); // 已拒绝
            // 拒绝提现时需要恢复用户余额
            userFinanceService.addBalance(entity.getUserId(), entity.getAmount());
        }

        entity.setRemark(auditNote);
        WithdrawalEntity updatedEntity = withdrawalRepository.save(entity);

        BankCardEntity bankCard = bankCardRepository.findById(updatedEntity.getBankCardId())
                .orElseThrow(() -> new BankCardException("银行卡信息不存在"));

        return convertToDTO(updatedEntity, bankCard);
    }

    /**
     * 标记提现为已打款
     */
    @Transactional
    public WithdrawalDTO markAsPaid(Long withdrawalId) {
        WithdrawalEntity entity = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new WithdrawalException("提现记录不存在"));

        if (entity.getStatus() != 2) {
            throw new WithdrawalException("提现申请状态不允许打款");
        }

        entity.setStatus(3); // 已打款

        WithdrawalEntity updatedEntity = withdrawalRepository.save(entity);

        BankCardEntity bankCard = bankCardRepository.findById(updatedEntity.getBankCardId())
                .orElseThrow(() -> new BankCardException("银行卡信息不存在"));

        return convertToDTO(updatedEntity, bankCard);
    }

    /**
     * 转换WithdrawalEntity到WithdrawalDTO
     */
    private WithdrawalDTO convertToDTO(WithdrawalEntity entity, BankCardEntity bankCard) {
        WithdrawalDTO dto = new WithdrawalDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setAmount(entity.getAmount());
        dto.setBankCardId(entity.getBankCardId());
        if (bankCard != null) {
            dto.setBankName(bankCard.getBankName());
            dto.setCardNumber(bankCard.getCardNumber());
            dto.setCardNumberMasked(maskCardNumber(bankCard.getCardNumber()));
            dto.setCardholderName(bankCard.getCardholderName());
        }
        dto.setStatus(entity.getStatus());
        dto.setStatusText(getStatusText(entity.getStatus()));
        dto.setAuditTime(entity.getAuditTime());
        dto.setPayoutTime(entity.getPayoutTime());
        dto.setRemark(entity.getRemark());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    /**
     * 根据ID查找提现记录
     */
    public WithdrawalEntity findById(Long id) {
        return withdrawalRepository.findById(id)
                .orElseThrow(() -> new WithdrawalException("提现记录不存在: " + id));
    }

    /**
     * 按状态分页查询（后台管理）
     */
    public Page<WithdrawalEntity> findByStatus(Integer status, Pageable pageable) {
        return withdrawalRepository.findByUserIdAndStatus(null, status, pageable);
    }

    /**
     * 查询所有（后台管理）
     */
    public Page<WithdrawalEntity> findAll(Pageable pageable) {
        return withdrawalRepository.findAll(pageable);
    }

    /**
     * 获取状态文本
     */
    public String getStatusText(Integer status) {
        switch (status) {
            case 1:
                return "待审核";
            case 2:
                return "已批准";
            case 3:
                return "已打款";
            case 4:
                return "已拒绝";
            default:
                return "未知状态";
        }
    }

    /**
     * 银行卡号脱敏
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return cardNumber;
        }

        int length = cardNumber.length();
        String prefix = cardNumber.substring(0, 4);
        String suffix = cardNumber.substring(length - 4);

        return prefix + " **** **** " + suffix;
    }
}
