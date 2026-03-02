package com.pengdeman.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置
 */
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WeChatConfig {

    /**
     * 小程序appId
     */
    private String appId;

    /**
     * 小程序appSecret
     */
    private String appSecret;

    /**
     * 微信登录接口地址
     */
    private String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 获取access_token接口地址
     */
    private String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getCode2SessionUrl() {
        return code2SessionUrl;
    }

    public void setCode2SessionUrl(String code2SessionUrl) {
        this.code2SessionUrl = code2SessionUrl;
    }

    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }
}