package com.pengdeman.config;

import com.pengdeman.service.AdminAuthService;
import com.pengdeman.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 * 从请求Header中提取JWT token，验证并设置Spring Security认证上下文
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final ObjectProvider<AdminAuthService> adminAuthServiceProvider;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ObjectProvider<AdminAuthService> adminAuthServiceProvider) {
        this.jwtUtil = jwtUtil;
        this.adminAuthServiceProvider = adminAuthServiceProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    // 检查是否是管理员
                    if (request.getRequestURI().startsWith("/api/admin/")) {
                        AdminAuthService adminAuthService = adminAuthServiceProvider.getObject();
                        if (adminAuthService.isAdmin(userId)) {
                            // 管理员认证成功
                            Authentication auth = new UsernamePasswordAuthenticationToken(
                                    userId, null, new ArrayList<>());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            log.debug("管理员认证成功，userId: {}", userId);
                        }
                    } else {
                        // 普通用户认证
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                userId, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.debug("用户认证成功，userId: {}", userId);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("JWT认证失败: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取token
     * 支持两种方式：
     * 1. Authorization: Bearer <token>
     * 2. 请求参数: ?token=<token>
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        String token = request.getParameter("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        return null;
    }
}