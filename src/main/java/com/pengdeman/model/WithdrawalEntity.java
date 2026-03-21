package com.pengdeman.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现申请实体类 - 存储提现信息
 */
@Entity
@Table(name = "withdrawals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 提现金额
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 银行卡ID
     */
    @Column(name = "bank_card_id", nullable = false)
    private Long bankCardId;

    /**
     * 状态：1-待审核，2-已批准，3-已打款，4-已拒绝
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 打款时间
     */
    @Column(name = "payout_time")
    private LocalDateTime payoutTime;

    /**
     * 审核备注/拒绝原因
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
