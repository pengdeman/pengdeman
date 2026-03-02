package com.pengdeman.exception;

/**
 * 微信登录异常
 */
public class WxLoginException extends RuntimeException {

    private Integer errCode;

    public WxLoginException(String message) {
        super(message);
    }

    public WxLoginException(Integer errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

    public Integer getErrCode() {
        return errCode;
    }
}