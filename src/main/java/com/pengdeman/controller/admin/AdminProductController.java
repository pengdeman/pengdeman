package com.pengdeman.controller.admin;

import com.pengdeman.dto.PageResponse;
import com.pengdeman.dto.ProductDTO;
import com.pengdeman.exception.ProductNotFoundException;
import com.pengdeman.model.ProductEntity;
import com.pengdeman.repository.ProductRepository;
import com.pengdeman.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台商品管理控制器
 */
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    public AdminProductController(ProductService productService,
                                  ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    /**
     * 获取商品列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageResponse<ProductDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductDTO> result = productService.getAllProducts(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getDetail(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * 新增商品
     */
    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO request) {
        ProductDTO created = productService.createProduct(request);
        return ResponseEntity.ok(created);
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO request) {
        ProductDTO updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取热门商品列表
     */
    @GetMapping("/hot")
    public ResponseEntity<List<ProductDTO>> getHotProducts() {
        List<ProductDTO> products = productService.getHotProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 添加商品到热门推荐
     */
    @PostMapping("/hot/{id}")
    public ResponseEntity<Void> addToHot(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer sortOrder) {
        productService.setHot(id, true, sortOrder);
        return ResponseEntity.ok().build();
    }

    /**
     * 从热门推荐移除
     */
    @DeleteMapping("/hot/{id}")
    public ResponseEntity<Void> removeFromHot(@PathVariable Long id) {
        productService.setHot(id, false, null);
        return ResponseEntity.ok().build();
    }
}
