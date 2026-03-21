package com.pengdeman.controller.admin;

import com.pengdeman.dto.PageResponse;
import com.pengdeman.dto.WithdrawalDTO;
import com.pengdeman.model.WithdrawalEntity;
import com.pengdeman.service.WithdrawalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台提现管理控制器
 */
@RestController
@RequestMapping("/api/admin/withdrawals")
public class AdminWithdrawalController {

    private final WithdrawalService withdrawalService;

    public AdminWithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    /**
     * 获取提现申请列表（按状态筛选）
     */
    @GetMapping
    public ResponseEntity<PageResponse<WithdrawalDTO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<WithdrawalEntity> pageResult;

        if (status != null) {
            pageResult = withdrawalService.findByStatus(status, pageable);
        } else {
            pageResult = withdrawalService.findAll(pageable);
        }

        List<WithdrawalDTO> dtoList = new ArrayList<>();
        for (WithdrawalEntity entity : pageResult.getContent()) {
            // 这里需要获取银行卡信息，实际需要service处理
            WithdrawalDTO dto = new WithdrawalDTO();
            dto.setId(entity.getId());
            dto.setUserId(entity.getUserId());
            dto.setAmount(entity.getAmount());
            dto.setBankCardId(entity.getBankCardId());
            dto.setStatus(entity.getStatus());
            dto.setStatusText(withdrawalService.getStatusText(entity.getStatus()));
            dto.setAuditTime(entity.getAuditTime());
            dto.setPayoutTime(entity.getPayoutTime());
            dto.setRemark(entity.getRemark());
            dto.setCreatedAt(entity.getCreatedAt());
            dtoList.add(dto);
        }

        PageResponse<WithdrawalDTO> response = new PageResponse<>(
                dtoList,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 获取提现申请详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<WithdrawalDTO> detail(@PathVariable Long id) {
        // 这里需要管理员可以查看任何用户的提现详情
        WithdrawalEntity entity = withdrawalService.findById(id);
        // 实际需要填充银行卡信息，简化返回
        WithdrawalDTO dto = new WithdrawalDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setAmount(entity.getAmount());
        dto.setBankCardId(entity.getBankCardId());
        dto.setStatus(entity.getStatus());
        dto.setStatusText(withdrawalService.getStatusText(entity.getStatus()));
        dto.setAuditTime(entity.getAuditTime());
        dto.setPayoutTime(entity.getPayoutTime());
        dto.setRemark(entity.getRemark());
        dto.setCreatedAt(entity.getCreatedAt());
        return ResponseEntity.ok(dto);
    }

    /**
     * 审核通过
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<WithdrawalDTO> approve(@PathVariable Long id, @RequestParam(required = false) String remark) {
        WithdrawalDTO dto = withdrawalService.approveWithdrawal(id, true, remark);
        return ResponseEntity.ok(dto);
    }

    /**
     * 审核拒绝
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<WithdrawalDTO> reject(@PathVariable Long id, @RequestParam(required = false) String remark) {
        WithdrawalDTO dto = withdrawalService.approveWithdrawal(id, false, remark);
        return ResponseEntity.ok(dto);
    }

    /**
     * 标记已打款
     */
    @PostMapping("/{id}/paid")
    public ResponseEntity<WithdrawalDTO> markPaid(@PathVariable Long id) {
        WithdrawalDTO dto = withdrawalService.markAsPaid(id);
        return ResponseEntity.ok(dto);
    }
}
