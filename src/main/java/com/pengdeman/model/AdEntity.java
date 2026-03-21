package com.pengdeman.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 广告实体 - 首页轮播广告
 */
@Entity
@Table(name = "ads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 广告标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 广告图片URL
     */
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    /**
     * 跳转链接
     */
    @Column(name = "link_url", length = 500)
    private String linkUrl;

    /**
     * 排序权重（越小越靠前）
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (enabled == null) {
            enabled = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
