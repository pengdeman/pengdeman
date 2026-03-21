package com.pengdeman.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 提现资格检查响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalEligibilityResponse {

    /**
     * 是否可以申请
     */
    private Boolean canApply;

    /**
     * 不可申请原因
     */
    private String reason;

    /**
     * 当前最大可提现金额
     */
    private BigDecimal maxAmount;

    /**
     * 最低提现金额要求
     */
    private BigDecimal minAmount;
}
