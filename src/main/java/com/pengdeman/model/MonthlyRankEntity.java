package com.pengdeman.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 月度排行实体 - 用户月度佣金排行榜
 */
@Entity
@Table(name = "monthly_rank", indexes = {
    @Index(name = "idx_year_month_rebate", columnList = "yearMonth,totalRebate DESC")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyRankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 年月，格式：YYYY-MM
     */
    @Column(name = "year_month", nullable = false, length = 7)
    private String yearMonth;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 月度总返利金额
     */
    @Column(name = "total_rebate", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRebate;

    /**
     * 排名
     */
    @Column(name = "rank_order")
    private Integer rankOrder;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (totalRebate == null) {
            totalRebate = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
