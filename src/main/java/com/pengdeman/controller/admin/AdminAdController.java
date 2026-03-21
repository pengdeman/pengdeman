package com.pengdeman.controller.admin;

import com.pengdeman.dto.AdDTO;
import com.pengdeman.model.AdEntity;
import com.pengdeman.service.AdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 后台广告管理控制器
 */
@RestController
@RequestMapping("/api/admin/ads")
public class AdminAdController {

    private final AdService adService;

    public AdminAdController(AdService adService) {
        this.adService = adService;
    }

    /**
     * 获取广告列表（分页）
     */
    @GetMapping
    public ResponseEntity<Page<AdEntity>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdEntity> result = adService.findAll(pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取广告详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdEntity> getDetail(@PathVariable Long id) {
        Optional<AdEntity> ad = adService.findById(id);
        if (ad.isPresent()) {
            return ResponseEntity.ok(ad.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 新增广告
     */
    @PostMapping
    public ResponseEntity<AdEntity> create(@RequestBody AdEntity ad) {
        AdEntity saved = adService.save(ad);
        return ResponseEntity.ok(saved);
    }

    /**
     * 更新广告
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdEntity> update(@PathVariable Long id, @RequestBody AdEntity ad) {
        ad.setId(id);
        AdEntity saved = adService.save(ad);
        return ResponseEntity.ok(saved);
    }

    /**
     * 删除广告
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adService.delete(id);
        return ResponseEntity.ok().build();
    }
}
