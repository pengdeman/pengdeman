package com.pengdeman.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengdeman.config.JdUnionConfig;
import com.pengdeman.dto.JdParseResponse;
import com.pengdeman.model.ProductEntity;
import com.pengdeman.repository.ProductRepository;
import com.pengdeman.service.SystemConfigService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 京东联盟API服务
 * 处理京东链接解析、商品信息查询、推广链接生成
 */
@Service
public class JdUnionService {

    private static final Logger log = LoggerFactory.getLogger(JdUnionService.class);

    private final JdUnionConfig jdUnionConfig;
    private final ProductRepository productRepository;
    private final SystemConfigService systemConfigService;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    public JdUnionService(JdUnionConfig jdUnionConfig,
                          ProductRepository productRepository,
                          SystemConfigService systemConfigService,
                          ObjectMapper objectMapper) {
        this.jdUnionConfig = jdUnionConfig;
        this.productRepository = productRepository;
        this.systemConfigService = systemConfigService;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    /**
     * 从京东链接中提取SKU ID
     * 支持格式:
     * https://item.jd.com/100012928458.html
     * https://www.jd.com/100012928458.html
     * https://item.jd.com/product/100012928458.html
     */
    public Optional<String> extractSkuId(String url) {
        if (url == null || url.isEmpty()) {
            return Optional.empty();
        }

        // 匹配数字模式，找到类似 100012928458 这样的商品ID
        Pattern pattern = Pattern.compile("(\\d+)\\.html");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        // 尝试匹配 /product/123456/
        pattern = Pattern.compile("/product/(\\d+)/?");
        matcher = pattern.matcher(url);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        // 尝试匹配任何连续数字
        pattern = Pattern.compile("(\\d{8,})");
        matcher = pattern.matcher(url);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Optional.empty();
    }

    /**
     * 解析京东链接，获取商品信息和预估返利
     */
    @Transactional
    public JdParseResponse parseUrl(String originalUrl) throws IOException {
        // 1. 提取SKU ID
        Optional<String> skuOpt = extractSkuId(originalUrl);
        if (!skuOpt.isPresent()) {
            throw new IllegalArgumentException("无法识别京东链接，请检查链接格式");
        }
        String skuId = skuOpt.get();

        // 2. 检查数据库是否已有缓存
        Optional<ProductEntity> productOpt = productRepository.findBySkuId(skuId);
        if (productOpt.isPresent()) {
            ProductEntity product = productOpt.get();
            return buildResponse(product);
        }

        // 3. 调用京东联盟API获取商品信息和佣金
        JdProductInfo productInfo = getProductInfo(skuId);
        if (productInfo == null) {
            throw new RuntimeException("获取商品信息失败，请检查京东联盟API配置");
        }

        // 4. 保存到数据库
        BigDecimal userRebateRate = systemConfigService.getUserRebateRate();
        BigDecimal estimatedUserRebate = productInfo.estimatedCommission
                .multiply(userRebateRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        String cpsUrl = generateCpsUrl(skuId);

        ProductEntity product = ProductEntity.builder()
                .skuId(skuId)
                .sku(skuId)
                .title(productInfo.productName)
                .productImage(productInfo.productImage)
                .originalPrice(productInfo.originalPrice)
                .estimatedCommission(productInfo.estimatedCommission)
                .userRebateRate(userRebateRate)
                .estimatedUserRebate(estimatedUserRebate)
                .cpsUrl(cpsUrl)
                .price(productInfo.originalPrice)
                .build();

        // 计算佣金比例
        if (productInfo.originalPrice != null && productInfo.estimatedCommission != null
                && productInfo.originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = productInfo.estimatedCommission
                    .divide(productInfo.originalPrice, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            product.setCommissionRate(rate);
        }

        product = productRepository.save(product);

        log.info("New product parsed: skuId={}, productName={}", skuId, productInfo.productName);

        return buildResponse(product);
    }

    /**
     * 调用京东联盟API获取商品信息
     */
    public JdProductInfo getProductInfo(String skuId) throws IOException {
        // 这里实现京东联盟API调用
        // 由于需要真实API权限，返回模拟数据供开发测试
        // 实际生产环境需要调用真实API

        log.info("Calling JD Union API for skuId: {}", skuId);

        // TODO: 实现真实京东联盟API调用
        // 示例返回模拟数据
        // 当配置了正确的API密钥后，替换为真实调用

        return JdProductInfo.builder()
                .skuId(skuId)
                .productName("京东商品 " + skuId)
                .productImage("")
                .originalPrice(new BigDecimal("100.00"))
                .estimatedCommission(new BigDecimal("5.00"))
                .build();
    }

    /**
     * 生成CPS推广链接
     */
    public String generateCpsUrl(String skuId) {
        // TODO: 调用京东联盟API生成真实推广链接
        // 目前返回拼接链接
        String promotionId = jdUnionConfig.getPromotionId();
        return "https://union.jd.com/promotion/" + promotionId + "?sku=" + skuId;
    }

    /**
     * 计算MD5签名（京东联盟需要）
     */
    private String md5(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(Integer.toHexString((b & 0xFF)));
        }
        return sb.toString();
    }

    /**
     * 构建响应
     */
    private JdParseResponse buildResponse(ProductEntity product) {
        BigDecimal userRebateRate = systemConfigService.getUserRebateRate();
        BigDecimal platformCommission = product.getEstimatedCommission();
        BigDecimal userRebate;

        if (product.getEstimatedUserRebate() != null) {
            userRebate = product.getEstimatedUserRebate();
        } else {
            userRebate = platformCommission != null
                    ? platformCommission.multiply(userRebateRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
        }

        return JdParseResponse.builder()
                .productId(product.getId())
                .skuId(product.getSkuId())
                .productName(product.getTitle())
                .productImage(product.getProductImage())
                .originalPrice(product.getOriginalPrice())
                .platformCommission(product.getEstimatedCommission())
                .userRebate(userRebate)
                .cpsUrl(product.getCpsUrl())
                .estimated(true)
                .build();
    }

    /**
     * 内部类 - 京东商品信息
     */
    @lombok.Data
    @lombok.Builder
    public static class JdProductInfo {
        private String skuId;
        private String productName;
        private String productImage;
        private BigDecimal originalPrice;
        private BigDecimal estimatedCommission;
    }
}
