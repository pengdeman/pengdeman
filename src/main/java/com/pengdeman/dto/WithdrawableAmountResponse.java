package com.pengdeman.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * 可提现金额响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawableAmountResponse {

    /**
     * 可提现金额
     */
    private BigDecimal withdrawableAmount;
}
