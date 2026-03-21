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
import java.util.Optional;

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
     * 获取热门商品列表（首页展示）
     */
    public List<ProductDTO> getHotProducts() {
        // 只获取热门且上架的商品，按排序返回
        List<ProductEntity> entities = productRepository
                .findByIsHotTrueAndStatusOrderByHotSortOrderAsc(1);

        List<ProductDTO> dtoList = new ArrayList<>();
        for (ProductEntity entity : entities) {
            dtoList.add(convertToDTO(entity));
        }
        return dtoList;
    }

    /**
     * 设置/取消商品热门推荐
     */
    @Transactional
    public void setHot(Long productId, boolean isHot, Integer sortOrder) {
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        entity.setIsHot(isHot);
        if (sortOrder != null) {
            entity.setHotSortOrder(sortOrder);
        }
        productRepository.save(entity);
    }

    /**
     * 根据SKU ID查找商品
     */
    public Optional<ProductEntity> findBySkuId(String skuId) {
        return productRepository.findBySkuId(skuId);
    }

    /**
     * 创建商品从京东解析
     */
    @Transactional
    public ProductEntity createFromJd(String skuId, String productName, String productImage,
                                      BigDecimal originalPrice, BigDecimal estimatedCommission,
                                      BigDecimal userRebateRate, String cpsUrl) {
        ProductEntity entity = new ProductEntity();
        entity.setSkuId(skuId);
        entity.setSku(skuId); // sku字段兼容存储
        entity.setTitle(productName);
        entity.setProductImage(productImage);
        entity.setOriginalPrice(originalPrice);
        entity.setEstimatedCommission(estimatedCommission);
        entity.setUserRebateRate(userRebateRate);
        entity.setEstimatedUserRebate(
                estimatedCommission.multiply(userRebateRate).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP)
        );
        entity.setCpsUrl(cpsUrl);
        entity.setIsHot(false);
        entity.setHotSortOrder(0);
        entity.setPrice(originalPrice);
        entity.setStatus(1);

        // 计算佣金率
        if (originalPrice != null && estimatedCommission != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = estimatedCommission.divide(originalPrice, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
            entity.setCommissionRate(rate);
        }

        return productRepository.save(entity);
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

        // 计算预估佣金和用户返利
        BigDecimal estimatedCommission = entity.getEstimatedCommission();
        if (estimatedCommission == null && entity.getPrice() != null && entity.getCommissionRate() != null) {
            estimatedCommission = entity.getPrice().multiply(entity.getCommissionRate().divide(BigDecimal.valueOf(100)));
        }

        if (estimatedCommission != null) {
            dto.setCommission(estimatedCommission.setScale(2, BigDecimal.ROUND_HALF_UP));

            BigDecimal userRebateRate = entity.getUserRebateRate();
            if (userRebateRate == null) {
                userRebateRate = new BigDecimal("20.00");
            }
            BigDecimal userRebate = estimatedCommission.multiply(userRebateRate).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            dto.setUserCommission(userRebate);
            dto.setEstimatedUserRebate(userRebate);
        }

        dto.setEstimatedCommission(estimatedCommission);
        dto.setEstimatedUserRebate(entity.getEstimatedUserRebate());
        dto.setCpsUrl(entity.getCpsUrl());
        dto.setIsHot(entity.getIsHot());
        dto.setProductImage(entity.getProductImage());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}
