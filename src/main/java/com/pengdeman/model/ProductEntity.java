package com.pengdeman.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品实体类 - 存储商品信息
 */
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 京东SKU
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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ProductEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getJdPrice() {
        return jdPrice;
    }

    public void setJdPrice(BigDecimal jdPrice) {
        this.jdPrice = jdPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
