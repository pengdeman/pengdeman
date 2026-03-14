package com.pengdeman.exception;

/**
 * 自定义异常 - 用户资金相关异常
 */
public class UserFinanceException extends RuntimeException {

    public UserFinanceException(String message) {
        super(message);
    }

    public UserFinanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
