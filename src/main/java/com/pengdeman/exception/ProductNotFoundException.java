package com.pengdeman.exception;

/**
 * 自定义异常 - 商品未找到
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(Long productId) {
        super("商品不存在: " + productId);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
