package com.pengdeman.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户资金实体类 - 存储用户资金信息
 */
@Entity
@Table(name = "user_finance")
public class UserFinanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 账户余额
     */
    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 总收入
     */
    @Column(name = "total_income", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalIncome = BigDecimal.ZERO;

    /**
     * 可提现金额
     */
    @Column(name = "withdrawable_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal withdrawableAmount = BigDecimal.ZERO;

    /**
     * 订单总数
     */
    @Column(name = "order_count", nullable = false)
    private Integer orderCount = 0;

    /**
     * 待提现金额
     */
    @Column(name = "pending_withdrawal", nullable = false, precision = 10, scale = 2)
    private BigDecimal pendingWithdrawal = BigDecimal.ZERO;

    /**
     * 已提现金额
     */
    @Column(name = "total_withdrawn", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalWithdrawn = BigDecimal.ZERO;

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

    public UserFinanceEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getWithdrawableAmount() {
        return withdrawableAmount;
    }

    public void setWithdrawableAmount(BigDecimal withdrawableAmount) {
        this.withdrawableAmount = withdrawableAmount;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getPendingWithdrawal() {
        return pendingWithdrawal;
    }

    public void setPendingWithdrawal(BigDecimal pendingWithdrawal) {
        this.pendingWithdrawal = pendingWithdrawal;
    }

    public BigDecimal getTotalWithdrawn() {
        return totalWithdrawn;
    }

    public void setTotalWithdrawn(BigDecimal totalWithdrawn) {
        this.totalWithdrawn = totalWithdrawn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
