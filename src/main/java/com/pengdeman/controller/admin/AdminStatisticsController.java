package com.pengdeman.controller.admin;

import com.pengdeman.dto.admin.StatisticsOverviewDTO;
import com.pengdeman.repository.OrderRepository;
import com.pengdeman.repository.UserRepository;
import com.pengdeman.repository.WithdrawalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 后台数据统计控制器
 */
@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final WithdrawalRepository withdrawalRepository;

    public AdminStatisticsController(UserRepository userRepository,
                                      OrderRepository orderRepository,
                                      WithdrawalRepository withdrawalRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.withdrawalRepository = withdrawalRepository;
    }

    /**
     * 获取概览统计数据
     */
    @GetMapping("/overview")
    public ResponseEntity<StatisticsOverviewDTO> getOverview() {
        LocalDate today = LocalDate.now();
        LocalDate startOfDay = today.atStartOfDay().toLocalDate();

        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();

        // 简单统计，实际可以按日期统计
        StatisticsOverviewDTO dto = StatisticsOverviewDTO.builder()
                .totalUsers(totalUsers)
                .totalOrders(totalOrders)
                .build();

        return ResponseEntity.ok(dto);
    }
}
