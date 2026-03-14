package com.pengdeman.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 商品推广链接响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionLinkResponse {

    /**
     * 推广链接
     */
    private String promotionUrl;

    /**
     * 短链接（可选）
     */
    private String shortUrl;

    /**
     * 点击链接（可选）
     */
    private String clickUrl;
}
