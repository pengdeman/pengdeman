package com.pengdeman.exception;

/**
 * 自定义异常 - 订单未找到
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(Long orderId) {
        super("订单不存在: " + orderId);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
