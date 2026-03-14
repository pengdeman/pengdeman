package com.pengdeman.controller;

import com.pengdeman.dto.PageResponse;
import com.pengdeman.dto.WithdrawalCreateRequest;
import com.pengdeman.dto.WithdrawalDTO;
import com.pengdeman.service.WithdrawalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 提现管理API控制器
 */
@RestController
@RequestMapping("/api/withdrawals")
@CrossOrigin(origins = "*")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    /**
     * 创建提现申请
     * 注意：实际使用时应该从token中解析userId，而不是从路径参数获取
     */
    @PostMapping
    public ResponseEntity<WithdrawalDTO> createWithdrawal(
            @RequestParam Long userId,
            @Valid @RequestBody WithdrawalCreateRequest request) {
        WithdrawalDTO withdrawal = withdrawalService.createWithdrawal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(withdrawal);
    }

    /**
     * 获取用户提现记录（分页）
     */
    @GetMapping
    public ResponseEntity<PageResponse<WithdrawalDTO>> getUserWithdrawals(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<WithdrawalDTO> withdrawals = withdrawalService.getUserWithdrawals(userId, status, page, size);
        return ResponseEntity.ok(withdrawals);
    }

    /**
     * 获取提现详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<WithdrawalDTO> getWithdrawalById(
            @RequestParam Long userId,
            @PathVariable Long id) {
        WithdrawalDTO withdrawal = withdrawalService.getWithdrawalById(userId, id);
        return ResponseEntity.ok(withdrawal);
    }

    /**
     * 审核提现申请
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<WithdrawalDTO> approveWithdrawal(
            @PathVariable Long id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String auditNote) {
        WithdrawalDTO withdrawal = withdrawalService.approveWithdrawal(id, approved, auditNote);
        return ResponseEntity.ok(withdrawal);
    }

    /**
     * 标记提现为已打款
     */
    @PutMapping("/{id}/mark-paid")
    public ResponseEntity<WithdrawalDTO> markAsPaid(@PathVariable Long id) {
        WithdrawalDTO withdrawal = withdrawalService.markAsPaid(id);
        return ResponseEntity.ok(withdrawal);
    }
}
