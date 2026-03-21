package com.pengdeman.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 京东联盟配置
 */
@Component
@ConfigurationProperties(prefix = "jd.union")
public class JdUnionConfig {

    /**
     * App Key
     */
    private String appKey;

    /**
     * App Secret
     */
    private String appSecret;

    /**
     * API Key
     */
    private String apiKey;

    /**
     * 推广位ID
     */
    private String promotionId;

    /**
     * API基础URL
     */
    private String baseUrl = "https://api.jd.com/routerjson";

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
