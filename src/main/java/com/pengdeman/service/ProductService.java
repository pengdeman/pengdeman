package com.pengdeman.service;

import com.pengdeman.dto.PageResponse;
import com.pengdeman.dto.ProductDTO;
import com.pengdeman.exception.ProductNotFoundException;
import com.pengdeman.model.ProductEntity;
import com.pengdeman.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品管理服务
 */
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 获取所有商品（分页）
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> productPage = productRepository.findAll(pageable);

        List<ProductDTO> productDTOs = new ArrayList<>();
        for (ProductEntity entity : productPage.getContent()) {
            productDTOs.add(convertToDTO(entity));
        }

        return new PageResponse<>(
                productDTOs,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    /**
     * 根据ID获取商品
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return convertToDTO(entity);
    }

    /**
     * 根据SKU获取商品
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductBySku(String sku) {
        ProductEntity entity = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("商品不存在: " + sku));

        return convertToDTO(entity);
    }

    /**
     * 根据标题搜索商品（分页）
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductDTO> searchProductsByTitle(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> productPage = productRepository.findByTitleContaining(keyword, pageable);

        List<ProductDTO> productDTOs = new ArrayList<>();
        for (ProductEntity entity : productPage.getContent()) {
            productDTOs.add(convertToDTO(entity));
        }

        return new PageResponse<>(
                productDTOs,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    /**
     * 创建商品
     */
    public ProductDTO createProduct(ProductDTO productDTO) {
        ProductEntity entity = new ProductEntity();
        entity.setSku(productDTO.getSku());
        entity.setTitle(productDTO.getTitle());
        entity.setDescription(productDTO.getDescription());
        entity.setPrice(productDTO.getPrice());
        entity.setCommissionRate(productDTO.getCommissionRate());
        entity.setImageUrl(productDTO.getImageUrl());
        entity.setCategoryId(productDTO.getCategoryId());
        entity.setJdPrice(productDTO.getJdPrice());
        entity.setOriginalPrice(productDTO.getOriginalPrice());
        entity.setSalesCount(productDTO.getSalesCount() != null ? productDTO.getSalesCount() : 0);
        entity.setStock(productDTO.getStock() != null ? productDTO.getStock() : 0);
        entity.setStatus(productDTO.getStatus() != null ? productDTO.getStatus() : 1);

        ProductEntity savedEntity = productRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    /**
     * 更新商品
     */
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        entity.setSku(productDTO.getSku());
        entity.setTitle(productDTO.getTitle());
        entity.setDescription(productDTO.getDescription());
        entity.setPrice(productDTO.getPrice());
        entity.setCommissionRate(productDTO.getCommissionRate());
        entity.setImageUrl(productDTO.getImageUrl());
        entity.setCategoryId(productDTO.getCategoryId());
        entity.setJdPrice(productDTO.getJdPrice());
        entity.setOriginalPrice(productDTO.getOriginalPrice());
        entity.setSalesCount(productDTO.getSalesCount());
        entity.setStock(productDTO.getStock());
        entity.setStatus(productDTO.getStatus());

        ProductEntity updatedEntity = productRepository.save(entity);
        return convertToDTO(updatedEntity);
    }

    /**
     * 删除商品
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);
    }

    /**
     * 获取商品推广链接（模拟京东联盟API调用）
     */
    public String getPromotionLink(String sku) {
        // 这里应该调用京东联盟API来获取真实的推广链接
        // 目前返回模拟链接
        return "https://u.jd.com/xxxxx?sku=" + sku;
    }

    /**
     * 转换ProductEntity到ProductDTO
     */
    private ProductDTO convertToDTO(ProductEntity entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setSku(entity.getSku());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setCommissionRate(entity.getCommissionRate());
        dto.setImageUrl(entity.getImageUrl());
        dto.setCategoryId(entity.getCategoryId());
        dto.setJdPrice(entity.getJdPrice());
        dto.setOriginalPrice(entity.getOriginalPrice());
        dto.setSalesCount(entity.getSalesCount());
        dto.setStock(entity.getStock());
        dto.setStatus(entity.getStatus());

        // 计算佣金相关信息
        if (entity.getPrice() != null && entity.getCommissionRate() != null) {
            BigDecimal commission = entity.getPrice().multiply(entity.getCommissionRate().divide(BigDecimal.valueOf(100)));
            dto.setCommission(commission.setScale(2, BigDecimal.ROUND_HALF_UP));
            // 用户返利比例：假设为佣金的20%
            dto.setUserCommission(commission.multiply(BigDecimal.valueOf(0.2)).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}
