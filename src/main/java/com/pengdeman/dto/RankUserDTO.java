package com.pengdeman.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 排行用户DTO - 排行榜返回数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankUserDTO {

    /**
     * 排行记录ID
     */
    private Long rankId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 年月
     */
    private String yearMonth;

    /**
     * 月度总返利
     */
    private BigDecimal totalRebate;

    /**
     * 排名
     */
    private Integer rankOrder;
}
