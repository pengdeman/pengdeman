package com.pengdeman.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 佣金记录响应DTO
 */
public class CommissionRecordDTO {

    private Long id;
    private Long userId;
    private Long orderId;
    private Long productId;
    private BigDecimal commissionAmount;
    private Integer status;
    private String statusText;
    private LocalDateTime settledTime;
    private LocalDateTime createdAt;

    public CommissionRecordDTO() {
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public LocalDateTime getSettledTime() {
        return settledTime;
    }

    public void setSettledTime(LocalDateTime settledTime) {
        this.settledTime = settledTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
