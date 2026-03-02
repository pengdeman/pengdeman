package com.pengdeman.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类 - 存储微信用户信息
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 微信openid - 用户唯一标识
     */
    @Column(name = "openid", nullable = false, unique = true, length = 100)
    private String openid;

    /**
     * 微信unionid - 同一主体下多应用用户统一标识
     */
    @Column(name = "unionid", unique = true, length = 100)
    private String unionid;

    /**
     * 微信昵称
     */
    @Column(name = "nickname", length = 100)
    private String nickname;

    /**
     * 头像URL
     */
    @Column(name = "avatar", length = 500)
    private String avatar;

    /**
     * 性别 0-未知 1-男 2-女
     */
    @Column(name = "gender")
    private Integer gender;

    /**
     * 国家
     */
    @Column(name = "country", length = 50)
    private String country;

    /**
     * 省份
     */
    @Column(name = "province", length = 50)
    private String province;

    /**
     * 城市
     */
    @Column(name = "city", length = 50)
    private String city;

    /**
     * 用户状态 0-禁用 1-正常
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UserEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}