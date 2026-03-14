package com.pengdeman.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 订单统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
