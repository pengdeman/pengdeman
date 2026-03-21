package com.pengdeman.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类 - 存储微信用户信息，同时支持管理员登录
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 微信openid - 用户唯一标识
     */
    @Column(name = "openid", unique = true, length = 100)
    private String openid;

    /**
     * 微信unionid - 同一主体下多应用用户统一标识
     */
    @Column(name = "unionid", unique = true, length = 100)
    private String unionid;

    /**
     * 用户名（管理员登录用，普通用户可为空）
     */
    @Column(name = "username", length = 50, unique = true)
    private String username;

    /**
     * 密码（加密存储，管理员登录用，普通用户可为空）
     */
    @Column(name = "password", length = 100)
    private String password;

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
     * 是否为管理员账号
     */
    @Column(name = "is_admin")
    private Boolean isAdmin;

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
        if (isAdmin == null) {
            isAdmin = false;
        }
        if (status == null) {
            status = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}