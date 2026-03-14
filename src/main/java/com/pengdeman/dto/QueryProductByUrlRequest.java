package com.pengdeman.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 通过URL查询商品请求DTO
 */
@Data
public class QueryProductByUrlRequest {

    /**
     * 京东商品链接
     */
    @NotBlank(message = "商品链接不能为空")
    private String url;
}
