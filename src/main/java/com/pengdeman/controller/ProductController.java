package com.pengdeman.controller;

import com.pengdeman.dto.*;
import com.pengdeman.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 产品管理API控制器
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 根据京东链接查询商品
     */
    @PostMapping("/query-by-url")
    public ResponseEntity<ProductDTO> queryProductByUrl(@Valid @RequestBody QueryProductByUrlRequest request) {
        // 这里应该调用service层方法，根据URL查询商品
        // 目前返回模拟数据
        ProductDTO product = createMockProduct();
        return ResponseEntity.ok(product);
    }

    /**
     * 获取所有商品（分页）
     */
    @GetMapping
    public ResponseEntity<PageResponse<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductDTO> products = productService.getAllProducts(page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据ID获取商品
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * 根据SKU获取商品
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable String sku) {
        ProductDTO product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    /**
     * 搜索商品（分页）
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductDTO>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductDTO> products = productService.searchProductsByTitle(keyword, page, size);
        return ResponseEntity.ok(products);
    }

    /**
     * 创建新商品
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO request) {
        ProductDTO createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO request) {
        ProductDTO updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取商品推广链接
     */
    @PostMapping("/{id}/promotion-link")
    public ResponseEntity<PromotionLinkResponse> getPromotionUrl(
            @PathVariable Long id,
            @RequestBody(required = false) PromotionLinkRequest request) {
        ProductDTO product = productService.getProductById(id);
        String promotionLink = productService.getPromotionLink(product.getSku());

        PromotionLinkResponse response = PromotionLinkResponse.builder()
                .promotionUrl(promotionLink)
                .shortUrl(promotionLink)
                .clickUrl(promotionLink)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 根据SKU获取推广链接
     */
    @PostMapping("/sku/{sku}/promotion-link")
    public ResponseEntity<PromotionLinkResponse> getPromotionLinkBySku(
            @PathVariable String sku,
            @RequestBody(required = false) PromotionLinkRequest request) {
        String promotionLink = productService.getPromotionLink(sku);
        PromotionLinkResponse response = PromotionLinkResponse.builder()
                .promotionUrl(promotionLink)
                .shortUrl(promotionLink)
                .clickUrl(promotionLink)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 获取热门商品列表（首页展示）
     */
    @GetMapping("/hot")
    public ResponseEntity<List<ProductDTO>> getHotProducts() {
        List<ProductDTO> products = productService.getHotProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 创建模拟商品数据
     */
    private ProductDTO createMockProduct() {
        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setSku("100012928458");
        product.setTitle("【京东自营】Apple iPhone 15 Pro Max (A3104) 256GB 钛金属");
        product.setPrice(new BigDecimal("9999"));
        product.setOriginalPrice(new BigDecimal("10999"));
        product.setCommissionRate(new BigDecimal("2.0"));
        product.setCommission(new BigDecimal("199.98"));
        product.setUserCommission(new BigDecimal("39.99"));
        product.setImageUrl("https://img.yzcdn.cn/vant/ipad.jpeg");
        product.setSalesCount(12890);
        product.setStatus(1);
        return product;
    }
}
