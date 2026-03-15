package com.pengdeman.controller;

import com.pengdeman.dto.UserFinanceDTO;
import com.pengdeman.dto.WithdrawableAmountResponse;
import com.pengdeman.dto.OrderStatsResponse;
import com.pengdeman.service.UserFinanceService;
import com.pengdeman.service.WeChatService;
import com.pengdeman.model.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 用户资金管理API控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserFinanceController {

    private final UserFinanceService userFinanceService;
    private final WeChatService weChatService;

    public UserFinanceController(UserFinanceService userFinanceService, WeChatService weChatService) {
        this.userFinanceService = userFinanceService;
        this.weChatService = weChatService;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<UserEntity> getCurrentUser() {
        // TODO: 从token中获取userId
        Long userId = 1L;
        UserEntity user = weChatService.getUserById(userId);
        user.setOpenid(null);
        user.setUnionid(null);
        return ResponseEntity.ok(user);
    }

    /**
     * 获取用户资金信息
     */
    @GetMapping("/finance")
    public ResponseEntity<UserFinanceDTO> getUserFinance() {
        // TODO: 从token中获取userId
        Long userId = 1L;
        UserFinanceDTO finance = userFinanceService.getUserFinance(userId);
        return ResponseEntity.ok(finance);
    }

    /**
     * 获取用户可提现金额
     */
    @GetMapping("/withdrawable-amount")
    public ResponseEntity<WithdrawableAmountResponse> getWithdrawableAmount() {
        // TODO: 从token中获取userId
        Long userId = 1L;
        BigDecimal amount = userFinanceService.getWithdrawableAmount(userId);
        WithdrawableAmountResponse response = new WithdrawableAmountResponse();
        response.setWithdrawableAmount(amount);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取订单统计
     */
    @GetMapping("/order-stats")
    public ResponseEntity<OrderStatsResponse> getOrderStats() {
        // TODO: 从token中获取userId
        Long userId = 1L;
        // TODO: 实现订单统计逻辑
        OrderStatsResponse response = new OrderStatsResponse(0, 0, 0, 0);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/current")
    public ResponseEntity<UserEntity> updateCurrentUser(@RequestBody UserEntity userInfo) {
        // TODO: 从token中获取userId
        Long userId = 1L;
        // TODO: 实现用户信息更新逻辑
        UserEntity user = weChatService.getUserById(userId);
        user.setOpenid(null);
        user.setUnionid(null);
        return ResponseEntity.ok(user);
    }
}
