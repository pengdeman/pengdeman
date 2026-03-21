package com.pengdeman.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现申请响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalDTO {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private Long bankCardId;
    private String bankName;
    private String cardNumber;
    private String cardNumberMasked;
    private String cardholderName;
    private Integer status;
    private String statusText;
    private LocalDateTime auditTime;
    private LocalDateTime payoutTime;
    private String remark;
    private LocalDateTime createdAt;
}
