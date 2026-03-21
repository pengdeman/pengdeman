package com.pengdeman.repository;

import com.pengdeman.model.MonthlyRankEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 月度排行数据访问接口
 */
@Repository
public interface MonthlyRankRepository extends JpaRepository<MonthlyRankEntity, Long> {

    /**
     * 查询指定月份排行榜，按返利降序排列
     */
    Page<MonthlyRankEntity> findByYearMonthAndTotalRebateGreaterThanOrderByTotalRebateDesc(
            String yearMonth, BigDecimal minRebate, Pageable pageable);

    /**
     * 查询指定月份用户排行记录
     */
    Optional<MonthlyRankEntity> findByYearMonthAndUserId(String yearMonth, Long userId);

    /**
     * 删除指定月份所有排行
     */
    void deleteByYearMonth(String yearMonth);

    /**
     * 判断指定月份是否存在
     */
    boolean existsByYearMonth(String yearMonth);
}
