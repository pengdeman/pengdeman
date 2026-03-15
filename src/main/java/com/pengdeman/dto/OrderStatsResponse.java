package com.pengdeman.dto;

/**
 * 订单统计响应DTO
 */
public class OrderStatsResponse {

    /**
     * 总订单数
     */
    private Integer totalOrders;

    /**
     * 待处理订单数
     */
    private Integer pendingOrders;

    /**
     * 已确认订单数
     */
    private Integer confirmedOrders;

    /**
     * 已完成订单数
     */
    private Integer completedOrders;

    public OrderStatsResponse() {
    }

    public OrderStatsResponse(Integer totalOrders, Integer pendingOrders, Integer confirmedOrders, Integer completedOrders) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.confirmedOrders = confirmedOrders;
        this.completedOrders = completedOrders;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Integer pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public Integer getConfirmedOrders() {
        return confirmedOrders;
    }

    public void setConfirmedOrders(Integer confirmedOrders) {
        this.confirmedOrders = confirmedOrders;
    }

    public Integer getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Integer completedOrders) {
        this.completedOrders = completedOrders;
    }
}
