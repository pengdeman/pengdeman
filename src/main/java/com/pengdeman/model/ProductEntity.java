package com.pengdeman.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品实体类 - 存储商品信息，支持京东返利商品
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 京东SKU ID
     */
    @Column(name = "sku_id", unique = true, length = 50)
    private String skuId;

    /**
     * 京东SKU（兼容原有字段）
     */
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    /**
     * 商品标题
     */
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    /**
     * 商品描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 商品价格
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 佣金比例（百分比）
     */
    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    /**
     * 商品主图URL
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /**
     * 京东商品图片URL（独立字段）
     */
    @Column(name = "product_image", length = 500)
    private String productImage;

    /**
     * 商品分类ID
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * 京东价
     */
    @Column(name = "jd_price", precision = 10, scale = 2)
    private BigDecimal jdPrice;

    /**
     * 原价
     */
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /**
     * 预估平台佣金
     */
    @Column(name = "estimated_commission", precision = 10, scale = 2)
    private BigDecimal estimatedCommission;

    /**
     * 用户返利比例（百分比，默认20%）
     */
    @Column(name = "user_rebate_rate", precision = 5, scale = 2)
    private BigDecimal userRebateRate;

    /**
     * 预估用户返利
     */
    @Column(name = "estimated_user_rebate", precision = 10, scale = 2)
    private BigDecimal estimatedUserRebate;

    /**
     * CPS推广链接
     */
    @Column(name = "cps_url", length = 500)
    private String cpsUrl;

    /**
     * 是否热门推荐
     */
    @Column(name = "is_hot")
    private Boolean isHot;

    /**
     * 热门排序
     */
    @Column(name = "hot_sort_order")
    private Integer hotSortOrder;

    /**
     * 销量
     */
    @Column(name = "sales_count", nullable = false)
    private Integer salesCount = 0;

    /**
     * 库存
     */
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    /**
     * 状态：0-下架，1-上架
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (userRebateRate == null) {
            userRebateRate = new BigDecimal("20.00");
        }
        if (isHot == null) {
            isHot = false;
        }
        if (hotSortOrder == null) {
            hotSortOrder = 0;
        }
        if (salesCount == null) {
            salesCount = 0;
        }
        if (stock == null) {
            stock = 0;
        }
        if (status == null) {
            status = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
