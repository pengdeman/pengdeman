package com.pengdeman.exception;

/**
 * 自定义异常 - 银行卡相关异常
 */
public class BankCardException extends RuntimeException {

    public BankCardException(String message) {
        super(message);
    }

    public BankCardException(String message, Throwable cause) {
        super(message, cause);
    }
}
