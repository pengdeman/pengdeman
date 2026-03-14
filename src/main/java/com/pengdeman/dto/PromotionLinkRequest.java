package com.pengdeman.dto;

import lombok.Data;

/**
 * 获取推广链接请求DTO
 */
@Data
public class PromotionLinkRequest {

    /**
     * 优惠券链接（可选）
     */
    private String couponUrl;
}
