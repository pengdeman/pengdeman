package com.pengdeman.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLoginResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 是否管理员
     */
    private Boolean isAdmin;
}
