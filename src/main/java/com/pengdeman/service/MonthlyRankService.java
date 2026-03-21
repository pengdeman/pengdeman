package com.pengdeman.service;

import com.pengdeman.dto.RankUserDTO;
import com.pengdeman.model.MonthlyRankEntity;
import com.pengdeman.model.UserEntity;
import com.pengdeman.repository.MonthlyRankRepository;
import com.pengdeman.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 月度排行服务
 */
@Service
public class MonthlyRankService {

    private static final Logger log = LoggerFactory.getLogger(MonthlyRankService.class);

    private final MonthlyRankRepository monthlyRankRepository;
    private final UserRepository userRepository;

    public MonthlyRankService(MonthlyRankRepository monthlyRankRepository,
                              UserRepository userRepository) {
        this.monthlyRankRepository = monthlyRankRepository;
        this.userRepository = userRepository;
    }

    /**
     * 获取当前年月字符串
     */
    public String getCurrentYearMonth() {
        LocalDate today = LocalDate.now();
        return DateTimeFormatter.ofPattern("yyyy-MM").format(today);
    }

    /**
     * 获取月度排行榜（只显示返利大于0的）
     */
    public Page<RankUserDTO> getMonthlyRank(String yearMonth, Pageable pageable) {
        if (yearMonth == null || yearMonth.isEmpty()) {
            yearMonth = getCurrentYearMonth();
        }

        // 查询，只显示返利大于0的
        Page<MonthlyRankEntity> rankPage = monthlyRankRepository
                .findByYearMonthAndTotalRebateGreaterThanOrderByTotalRebateDesc(
                        yearMonth, BigDecimal.ZERO, pageable);

        // 转换为DTO，填充用户信息
        return rankPage.map(rank -> {
            Optional<UserEntity> userOpt = userRepository.findById(rank.getUserId());
            RankUserDTO dto = new RankUserDTO();
            dto.setRankId(rank.getId());
            dto.setYearMonth(rank.getYearMonth());
            dto.setTotalRebate(rank.getTotalRebate());
            dto.setRankOrder(rank.getRankOrder());
            userOpt.ifPresent(user -> {
                dto.setUserId(user.getId());
                dto.setNickname(user.getNickname());
                dto.setAvatar(user.getAvatar());
            });
            return dto;
        });
    }

    /**
     * 更新用户月度返利（订单结算时调用）
     */
    @Transactional
    public void updateUserRebate(Long userId, BigDecimal addRebate, String yearMonth) {
        if (yearMonth == null) {
            yearMonth = getCurrentYearMonth();
        }

        MonthlyRankEntity rank = monthlyRankRepository
                .findByYearMonthAndUserId(yearMonth, userId)
                .orElse(null);

        if (rank == null) {
            rank = MonthlyRankEntity.builder()
                    .yearMonth(yearMonth)
                    .userId(userId)
                    .totalRebate(addRebate)
                    .build();
        } else {
            rank.setTotalRebate(rank.getTotalRebate().add(addRebate));
        }

        monthlyRankRepository.save(rank);
        log.info("Monthly rank updated: userId={}, yearMonth={}, totalRebate={}",
                userId, yearMonth, rank.getTotalRebate());
    }

    /**
     * 创建新月份（每月1号调用）
     * 不需要清空，自然累积就是新的了
     */
    @Transactional
    public void ensureCurrentMonth() {
        String yearMonth = getCurrentYearMonth();
        if (!monthlyRankRepository.existsByYearMonth(yearMonth)) {
            log.info("New month started: {}", yearMonth);
            // 不需要预先创建，用户有交易的时候自动创建
        }
    }

    /**
     * 重新计算所有排名序号（可选，数据不准的时候可以调用
     */
    @Transactional
    public void recalculateRankOrder(String yearMonth) {
        List<MonthlyRankEntity> allRanks = new ArrayList<>();
        // 按返利排序后重新分配排名
        monthlyRankRepository.findByYearMonthAndTotalRebateGreaterThanOrderByTotalRebateDesc(
                yearMonth, BigDecimal.ZERO, Pageable.unpaged())
                .forEach(allRanks::add);

        int rank = 1;
        for (MonthlyRankEntity entity : allRanks) {
            entity.setRankOrder(rank++);
            monthlyRankRepository.save(entity);
        }
        log.info("Rank recalculated for {}: {} users", yearMonth, allRanks.size());
    }
}
