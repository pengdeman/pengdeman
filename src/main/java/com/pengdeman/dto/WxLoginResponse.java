package com.pengdeman.dto;

import java.math.BigDecimal;

/**
 * 微信登录响应DTO
 */
public class WxLoginResponse {

    /**
     * 登录token，用于后续接口认证
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否为新注册用户
     */
    private Boolean isNewUser;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 总收入
     */
    private BigDecimal totalIncome;

    /**
     * 可提现金额
     */
    private BigDecimal withdrawableAmount;

    /**
     * 订单总数
     */
    private Integer orderCount;

    public WxLoginResponse() {
    }

    private WxLoginResponse(Builder builder) {
        this.token = builder.token;
        this.userId = builder.userId;
        this.openid = builder.openid;
        this.nickname = builder.nickname;
        this.avatar = builder.avatar;
        this.isNewUser = builder.isNewUser;
        this.balance = builder.balance;
        this.totalIncome = builder.totalIncome;
        this.withdrawableAmount = builder.withdrawableAmount;
        this.orderCount = builder.orderCount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(Boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getWithdrawableAmount() {
        return withdrawableAmount;
    }

    public void setWithdrawableAmount(BigDecimal withdrawableAmount) {
        this.withdrawableAmount = withdrawableAmount;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private Long userId;
        private String openid;
        private String nickname;
        private String avatar;
        private Boolean isNewUser;
        private BigDecimal balance;
        private BigDecimal totalIncome;
        private BigDecimal withdrawableAmount;
        private Integer orderCount;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder openid(String openid) {
            this.openid = openid;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder isNewUser(Boolean isNewUser) {
            this.isNewUser = isNewUser;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder totalIncome(BigDecimal totalIncome) {
            this.totalIncome = totalIncome;
            return this;
        }

        public Builder withdrawableAmount(BigDecimal withdrawableAmount) {
            this.withdrawableAmount = withdrawableAmount;
            return this;
        }

        public Builder orderCount(Integer orderCount) {
            this.orderCount = orderCount;
            return this;
        }

        public WxLoginResponse build() {
            return new WxLoginResponse(this);
        }
    }
}
