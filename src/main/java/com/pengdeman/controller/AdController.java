package com.pengdeman.controller;

import com.pengdeman.dto.AdDTO;
import com.pengdeman.model.AdEntity;
import com.pengdeman.service.AdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 广告控制器 - 用户端公开接口
 */
@RestController
@RequestMapping("/api/ads")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    /**
     * 获取首页启用的广告列表（按排序）
     */
    @GetMapping("/list")
    public ResponseEntity<List<AdDTO>> getEnabledAds() {
        List<AdEntity> ads = adService.getEnabledAds();
        List<AdDTO> dtoList = ads.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    private AdDTO convertToDTO(AdEntity entity) {
        AdDTO dto = new AdDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setImageUrl(entity.getImageUrl());
        dto.setLinkUrl(entity.getLinkUrl());
        dto.setSortOrder(entity.getSortOrder());
        dto.setEnabled(entity.getEnabled());
        return dto;
    }
}
