package com.pengdeman.model;

/**
 * 订单状态枚举 - 京东返利订单状态
 * PENDING - 待确认（已下单，京东未结算）
 * CONFIRMED - 已确认（佣金已结算给平台）
 * PAID - 已返利（返利已入账用户账户）
 * CANCELLED - 已取消（订单取消，无返利）
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PAID,
    CANCELLED
}
