package com.pengdeman.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员登录请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLoginRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
