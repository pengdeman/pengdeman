package com.pengdeman.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 京东链接解析请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JdParseRequest {

    /**
     * 原始京东链接
     */
    private String originalUrl;
}
