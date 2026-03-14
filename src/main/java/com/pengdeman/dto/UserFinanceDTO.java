package com.pengdeman.dto;

import java.math.BigDecimal;

/**
 * 用户资金响应DTO
 */
public class UserFinanceDTO {

    private Long id;
    private Long userId;
    private BigDecimal balance;
    private BigDecimal totalIncome;
    private BigDecimal withdrawableAmount;
    private Integer orderCount;
    private BigDecimal pendingWithdrawal;
    private BigDecimal totalWithdrawn;

    public UserFinanceDTO() {
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
}
