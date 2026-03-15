package com.pengdeman.dto;

import java.math.BigDecimal;

/**
 * 可提现金额响应DTO
 */
public class WithdrawableAmountResponse {

    /**
     * 可提现金额
     */
    private BigDecimal withdrawableAmount;

    public WithdrawableAmountResponse() {
    }

    public WithdrawableAmountResponse(BigDecimal withdrawableAmount) {
        this.withdrawableAmount = withdrawableAmount;
    }

    public BigDecimal getWithdrawableAmount() {
        return withdrawableAmount;
    }

    public void setWithdrawableAmount(BigDecimal withdrawableAmount) {
        this.withdrawableAmount = withdrawableAmount;
    }
}
