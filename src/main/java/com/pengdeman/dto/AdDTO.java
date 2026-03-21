package com.pengdeman.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 广告DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdDTO {

    /**
     * 广告ID
     */
    private Long id;

    /**
     * 广告标题
     */
    private String title;

    /**
     * 广告图片URL
     */
    private String imageUrl;

    /**
     * 跳转链接
     */
    private String linkUrl;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
