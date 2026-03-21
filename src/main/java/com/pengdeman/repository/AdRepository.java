package com.pengdeman.repository;

import com.pengdeman.model.AdEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 广告数据访问接口
 */
@Repository
public interface AdRepository extends JpaRepository<AdEntity, Long> {

    /**
     * 获取所有启用的广告，按排序顺序
     */
    List<AdEntity> findByEnabledTrueOrderBySortOrderAsc();

    /**
     * 分页查询所有广告（管理端）
     */
    Page<AdEntity> findAllByOrderBySortOrderAsc(Pageable pageable);
}
