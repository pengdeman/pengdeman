package com.pengdeman.service;

import com.pengdeman.config.JdUnionConfig;
import com.pengdeman.model.MonthlyRankEntity;
import com.pengdeman.model.OrderEntity;
import com.pengdeman.model.OrderStatus;
import com.pengdeman.model.UserFinanceEntity;
import com.pengdeman.repository.MonthlyRankRepository;
import com.pengdeman.repository.OrderRepository;
import com.pengdeman.repository.UserFinanceRepository;
import com.pengdeman.service.MonthlyRankService;
import com.pengdeman.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 京东联盟订单同步服务
 * 定时同步订单状态，结算返利到用户账户
 */
@Service
public class JdUnionOrderSyncService {

    private static final Logger log = LoggerFactory.getLogger(JdUnionOrderSyncService.class);

    private final JdUnionConfig jdUnionConfig;
    private final OrderRepository orderRepository;
    private final UserFinanceRepository userFinanceRepository;
    private final MonthlyRankService monthlyRankService;
    private final SystemConfigService systemConfigService;

    public JdUnionOrderSyncService(JdUnionConfig jdUnionConfig,
                                  OrderRepository orderRepository,
                                  UserFinanceRepository userFinanceRepository,
                                  MonthlyRankService monthlyRankService,
                                  SystemConfigService systemConfigService) {
        this.jdUnionConfig = jdUnionConfig;
        this.orderRepository = orderRepository;
        this.userFinanceRepository = userFinanceRepository;
        this.monthlyRankService = monthlyRankService;
        this.systemConfigService = systemConfigService;
    }

    /**
     * 同步最近30天的订单
     */
    @Transactional
    public void syncRecentOrders() {
        log.info("Starting JD order sync...");

        // TODO: 实际调用京东联盟API查询最近订单
        // 这里只定义接口结构，等待有API权限后实现真实调用

        int syncedCount = 0;
        // 模拟查询逻辑
        // List<JdOrder> orders = callJdApi...

        // 对于每个已结算订单，更新状态并返利入账
        // for (JdOrder jdOrder : orders) {
        //     processSyncedOrder(jdOrder);
        //     syncedCount++;
        // }

        log.info("JD order sync completed, synced {} orders", syncedCount);
    }

    /**
     * 处理已同步确认的订单，返利入账
     */
    @Transactional
    public void processConfirmedOrder(OrderEntity order, BigDecimal platformCommission) {
        // 只处理待确认订单
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            return;
        }

        BigDecimal userRebateRate = systemConfigService.getUserRebateRate();
        BigDecimal userRebate = platformCommission
                .multiply(userRebateRate)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

        // 更新订单状态
        order.setPlatformCommission(platformCommission);
        order.setUserRebateAmount(userRebate);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setSettledTime(LocalDateTime.now());
        orderRepository.save(order);

        // 返利入账到用户账户
        UserFinanceEntity finance = userFinanceRepository.findByUserId(order.getUserId())
                .orElse(null);
        if (finance != null) {
            finance.setWithdrawableAmount(finance.getWithdrawableAmount().add(userRebate));
            finance.setTotalIncome(finance.getTotalIncome().add(userRebate));
            userFinanceRepository.save(finance);

            // 更新月度排行榜
            String yearMonth = monthlyRankService.getCurrentYearMonth();
            monthlyRankService.updateUserRebate(order.getUserId(), userRebate, yearMonth);

            log.info("Order settled: orderId={}, userId={}, platformCommission={}, userRebate={}",
                    order.getId(), order.getUserId(), platformCommission, userRebate);
        }
    }

    /**
     * 标记已返利到账
     */
    @Transactional
    public void markOrderPaid(OrderEntity order) {
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }
}
