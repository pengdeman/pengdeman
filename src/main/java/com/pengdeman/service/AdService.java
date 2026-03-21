package com.pengdeman.service;

import com.pengdeman.model.AdEntity;
import com.pengdeman.repository.AdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 广告服务
 */
@Service
public class AdService {

    private static final Logger log = LoggerFactory.getLogger(AdService.class);

    private final AdRepository adRepository;

    public AdService(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    /**
     * 获取前端首页启用的广告列表（按排序）
     */
    public List<AdEntity> getEnabledAds() {
        return adRepository.findByEnabledTrueOrderBySortOrderAsc();
    }

    /**
     * 管理端分页查询广告
     */
    public Page<AdEntity> findAll(Pageable pageable) {
        return adRepository.findAllByOrderBySortOrderAsc(pageable);
    }

    /**
     * 根据ID查找
     */
    public Optional<AdEntity> findById(Long id) {
        return adRepository.findById(id);
    }

    /**
     * 新增/保存广告
     */
    @Transactional
    public AdEntity save(AdEntity ad) {
        return adRepository.save(ad);
    }

    /**
     * 删除广告
     */
    @Transactional
    public void delete(Long id) {
        adRepository.deleteById(id);
        log.info("Ad deleted: id={}", id);
    }
}
