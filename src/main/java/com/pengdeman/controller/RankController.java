package com.pengdeman.controller;

import com.pengdeman.dto.RankUserDTO;
import com.pengdeman.service.MonthlyRankService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 排行榜控制器
 */
@RestController
@RequestMapping("/api/rank")
public class RankController {

    private final MonthlyRankService monthlyRankService;

    public RankController(MonthlyRankService monthlyRankService) {
        this.monthlyRankService = monthlyRankService;
    }

    /**
     * 获取月度佣金排行榜
     * @param yearMonth 年月，格式YYYY-MM，不传默认当前月
     */
    @GetMapping("/monthly")
    public ResponseEntity<Page<RankUserDTO>> getMonthlyRank(
            @RequestParam(required = false) String yearMonth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RankUserDTO> result = monthlyRankService.getMonthlyRank(yearMonth, pageable);
        return ResponseEntity.ok(result);
    }
}
