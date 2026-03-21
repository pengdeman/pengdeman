package com.pengdeman.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 默认管理员配置
 */
@Component
@ConfigurationProperties(prefix = "admin.default")
public class AdminConfig {

    /**
     * 默认管理员用户名
     */
    private String username = "admin";

    /**
     * 默认管理员密码
     */
    private String password = "admin123";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
