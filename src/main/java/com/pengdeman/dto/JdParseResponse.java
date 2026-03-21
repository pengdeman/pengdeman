package com.pengdeman.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 京东链接解析响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JdParseResponse {

    /**
     * 商品ID（平台内ID）
     */
    private Long productId;

    /**
     * 京东SKU ID
     */
    private String skuId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 商品原价
     */
    private BigDecimal originalPrice;

    /**
     * 平台预估佣金（京东给平台的）
     */
    private BigDecimal platformCommission;

    /**
     * 用户可得预估返利（平台佣金 × 返利比例）
     */
    private BigDecimal userRebate;

    /**
     * 生成的CPS推广链接
     */
    private String cpsUrl;

    /**
     * 是否为预估（true=预估，false=实际结算）
     */
    private Boolean estimated;
}
