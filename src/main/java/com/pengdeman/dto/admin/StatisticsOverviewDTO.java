package com.pengdeman.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 概览统计数据DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsOverviewDTO {

    /**
     * 总用户数
     */
    private long totalUsers;

    /**
     * 今日新增用户
     */
    private long todayNewUsers;

    /**
     * 总订单数
     */
    private long totalOrders;

    /**
     * 今日新增订单
     */
    private long todayNewOrders;

    /**
     * 今日结算佣金
     */
    private BigDecimal todayCommission;

    /**
     * 待审核提现申请数
     */
    private long pendingWithdrawals;
}
