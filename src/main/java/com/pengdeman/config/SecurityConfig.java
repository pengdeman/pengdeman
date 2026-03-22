package com.pengdeman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;

/**
 * Spring Security配置
 * 配置接口权限：公开接口允许匿名访问，需要认证的接口要求token
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/*.html").permitAll()
                .antMatchers("/**/*.js").permitAll()
                .antMatchers("/**/*.css").permitAll()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/admin.html").permitAll()
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
