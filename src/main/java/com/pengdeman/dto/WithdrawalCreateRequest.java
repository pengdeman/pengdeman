package com.pengdeman.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 提现申请请求DTO
 */
public class WithdrawalCreateRequest {

    @NotNull(message = "提现金额不能为空")
    @Positive(message = "提现金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "银行卡ID不能为空")
    private Long bankCardId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getBankCardId() {
        return bankCardId;
    }

    public void setBankCardId(Long bankCardId) {
        this.bankCardId = bankCardId;
    }
}
