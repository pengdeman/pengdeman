package com.pengdeman.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 订单创建请求DTO
 */
public class OrderCreateRequest {

    @NotBlank(message = "SKU不能为空")
    private String sku;

    @NotBlank(message = "商品标题不能为空")
    private String title;

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "佣金比例不能为空")
    @Positive(message = "佣金比例必须大于0")
    private BigDecimal commissionRate;

    @NotNull(message = "佣金金额不能为空")
    @Positive(message = "佣金金额必须大于0")
    private BigDecimal commission;

    @NotNull(message = "用户返利金额不能为空")
    @Positive(message = "用户返利金额必须大于0")
    private BigDecimal userCommission;

    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于0")
    private Integer quantity;

    @NotBlank(message = "推广链接不能为空")
    private String promotionLink;

    @NotBlank(message = "商品图片不能为空")
    private String productImage;

    private String paymentMethod = "微信支付";

    private String orderType = "jd";

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

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
