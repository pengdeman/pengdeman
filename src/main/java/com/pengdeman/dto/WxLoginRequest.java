package com.pengdeman.dto;

import javax.validation.constraints.NotBlank;

/**
 * 微信小程序登录请求DTO
 */
public class WxLoginRequest {

    /**
     * 微信登录code，由小程序端调用 wx.login() 获取
     */
    @NotBlank(message = "code不能为空")
    private String code;

    /**
     * 用户昵称（可选，获取用户信息后传入）
     */
    private String nickname;

    /**
     * 用户头像URL（可选）
     */
    private String avatar;

    /**
     * 性别（可选）
     */
    private Integer gender;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
}