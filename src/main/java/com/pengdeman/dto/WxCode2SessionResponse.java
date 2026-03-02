package com.pengdeman.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 微信code2session接口响应DTO
 * 对应微信官方API返回格式
 */
public class WxCode2SessionResponse {

    /**
     * 用户唯一标识
     */
    @JsonProperty("openid")
    private String openid;

    /**
     * 用户在开放平台的唯一标识符
     */
    @JsonProperty("unionid")
    private String unionid;

    /**
     * 会话密钥
     */
    @JsonProperty("session_key")
    private String sessionKey;

    /**
     * 错误码
     * -1: 系统繁忙
     * 0: 请求成功
     * 40029: code无效
     * 45011: 频率限制
     * 40226: 用户未绑定
     */
    @JsonProperty("errcode")
    private Integer errCode;

    /**
     * 错误信息
     */
    @JsonProperty("errmsg")
    private String errMsg;

    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        return errCode == null || errCode == 0;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}