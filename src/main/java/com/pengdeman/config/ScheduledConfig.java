package com.pengdeman.config;

import com.pengdeman.service.MonthlyRankService;
import com.pengdeman.service.JdUnionOrderSyncService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
public class ScheduledConfig {

    private final MonthlyRankService monthlyRankService;
    private final JdUnionOrderSyncService jdUnionOrderSyncService;

    public ScheduledConfig(MonthlyRankService monthlyRankService,
                          JdUnionOrderSyncService jdUnionOrderSyncService) {
        this.monthlyRankService = monthlyRankService;
        this.jdUnionOrderSyncService = jdUnionOrderSyncService;
    }

    /**
     * 每月1号凌晨0点10分确保当前月份排行榜已创建
     */
    @Scheduled(cron = "10 0 0 1 * *")
    public void monthlyRankInit() {
        monthlyRankService.ensureCurrentMonth();
        System.out.println("Monthly rank initialized for: " + monthlyRankService.getCurrentYearMonth());
    }

    /**
     * 每天凌晨2点同步京东订单状态
     * 同步最近30天的订单，更新结算状态和返利
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void syncJdOrders() {
        jdUnionOrderSyncService.syncRecentOrders();
    }
}
