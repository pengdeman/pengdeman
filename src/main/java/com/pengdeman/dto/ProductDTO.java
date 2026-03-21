package com.pengdeman.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String sku;
    private String skuId;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal commissionRate;
    private String imageUrl;
    private String productImage;
    private Long categoryId;
    private BigDecimal jdPrice;
    private BigDecimal originalPrice;
    private Integer salesCount;
    private Integer stock;
    private Integer status;
    private BigDecimal commission;
    private BigDecimal userCommission;
    private BigDecimal estimatedCommission;
    private BigDecimal estimatedUserRebate;
    private String cpsUrl;
    private Boolean isHot;
    private LocalDateTime createdAt;
}
