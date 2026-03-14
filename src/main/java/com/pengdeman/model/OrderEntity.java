package com.pengdeman.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类 - 存储订单信息
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号
     */
    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    private String orderNo;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 商品ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * SKU
     */
    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    /**
     * 商品标题
     */
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    /**
     * 商品价格
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 佣金金额
     */
    @Column(name = "commission", nullable = false, precision = 10, scale = 2)
    private BigDecimal commission;

    /**
     * 用户返利金额
     */
    @Column(name = "user_commission", precision = 10, scale = 2)
    private BigDecimal userCommission;

    /**
     * 数量
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    /**
     * 总金额
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 推广链接
     */
    @Column(name = "promotion_link", length = 1000)
    private String promotionLink;

    /**
     * 商品图片
     */
    @Column(name = "product_image", length = 500)
    private String productImage;

    /**
     * 订单状态：1-待支付，2-已支付，3-已发货，4-已收货，5-已取消
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 支付方式
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * 支付时间
     */
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    /**
     * 发货时间
     */
    @Column(name = "shipping_time")
    private LocalDateTime shippingTime;

    /**
     * 收货时间
     */
    @Column(name = "receipt_time")
    private LocalDateTime receiptTime;

    /**
     * 取消时间
     */
    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    /**
     * 订单类型：jd-京东
     */
    @Column(name = "order_type", length = 20)
    private String orderType = "jd";

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

    public OrderEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getUserCommission() {
        return userCommission;
    }

    public void setUserCommission(BigDecimal userCommission) {
        this.userCommission = userCommission;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPromotionLink() {
        return promotionLink;
    }

    public void setPromotionLink(String promotionLink) {
        this.promotionLink = promotionLink;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public LocalDateTime getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(LocalDateTime shippingTime) {
        this.shippingTime = shippingTime;
    }

    public LocalDateTime getReceiptTime() {
        return receiptTime;
    }

    public void setReceiptTime(LocalDateTime receiptTime) {
        this.receiptTime = receiptTime;
    }

    public LocalDateTime getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(LocalDateTime cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
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
