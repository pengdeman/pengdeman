package com.pengdeman.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类 - 存储京东返利订单信息
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 京东订单ID
     */
    @Column(name = "jd_order_id", length = 50)
    private String jdOrderId;

    /**
     * 订单号（平台生成）
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
     * 京东SKU ID
     */
    @Column(name = "sku_id", length = 50)
    private String skuId;

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
     * 平台实际佣金（京东结算给平台）
     */
    @Column(name = "platform_commission", precision = 10, scale = 2)
    private BigDecimal platformCommission;

    /**
     * 用户返利金额（平台佣金 × 返利比例）
     */
    @Column(name = "user_rebate_amount", precision = 10, scale = 2)
    private BigDecimal userRebateAmount;

    /**
     * 佣金金额（兼容原有字段）
     */
    @Column(name = "commission", nullable = false, precision = 10, scale = 2)
    private BigDecimal commission;

    /**
     * 用户返利金额（兼容原有字段）
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
     * 推广链接（CPS链接）
     */
    @Column(name = "promotion_link", length = 1000)
    private String promotionLink;

    /**
     * 商品图片
     */
    @Column(name = "product_image", length = 500)
    private String productImage;

    /**
     * 京东返利订单状态（新增枚举状态）
     */
    @Column(name = "order_status", length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    /**
     * 结算时间（京东结算佣金时间）
     */
    @Column(name = "settled_time")
    private LocalDateTime settledTime;

    /**
     * 原有订单状态：1-待支付，2-已支付，3-已发货，4-已收货，5-已取消（兼容）
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
        if (quantity == null) {
            quantity = 1;
        }
        if (status == null) {
            status = 1;
        }
        if (orderType == null) {
            orderType = "jd";
        }
        if (orderStatus == null) {
            orderStatus = OrderStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
