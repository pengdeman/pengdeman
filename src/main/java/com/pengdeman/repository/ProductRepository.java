package com.pengdeman.repository;

import com.pengdeman.model.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 产品数据访问接口
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * 根据SKU查找商品
     */
    Optional<ProductEntity> findBySku(String sku);

    /**
     * 根据标题模糊搜索商品（分页）
     */
    Page<ProductEntity> findByTitleContaining(String title, Pageable pageable);

    /**
     * 根据分类ID查找商品（分页）
     */
    Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 根据状态查找商品（分页）
     */
    Page<ProductEntity> findByStatus(Integer status, Pageable pageable);

    /**
     * 判断SKU是否存在
     */
    boolean existsBySku(String sku);
}
