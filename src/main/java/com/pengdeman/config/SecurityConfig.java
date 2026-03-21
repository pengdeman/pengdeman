package com.pengdeman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security配置
 * 配置接口权限：公开接口允许匿名访问，需要认证的接口要求token
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF
                .csrf().disable()
                // 基于token，不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置权限
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/admin/auth/login").permitAll()
                .antMatchers("/api/demo/**").permitAll()
                .antMatchers("/api/ads/**").permitAll()
                .antMatchers("/api/products/**").permitAll()
                .antMatchers("/api/rank/**").permitAll()
                .antMatchers("/api/jd/**").permitAll()
                .antMatchers("/api/orders/**").authenticated()
                .antMatchers("/api/users/**").authenticated()
                .antMatchers("/api/withdrawals/**").authenticated()
                .antMatchers("/api/bank-cards/**").authenticated()
                .antMatchers("/api/admin/**").authenticated()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // 允许H2控制台的frame
                .headers().frameOptions().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
