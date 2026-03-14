package com.pengdeman.exception;

/**
 * 自定义异常 - 提现相关异常
 */
public class WithdrawalException extends RuntimeException {

    public WithdrawalException(String message) {
        super(message);
    }

    public WithdrawalException(String message, Throwable cause) {
        super(message, cause);
    }
}
