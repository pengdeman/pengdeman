package com.pengdeman.controller;

import com.pengdeman.dto.BankCardDTO;
import com.pengdeman.dto.BankCardCreateRequest;
import com.pengdeman.service.BankCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 银行卡管理API控制器
 */
@RestController
@RequestMapping("/api/bank-cards")
@CrossOrigin(origins = "*")
public class BankCardController {

    private final BankCardService bankCardService;

    public BankCardController(BankCardService bankCardService) {
        this.bankCardService = bankCardService;
    }

    /**
     * 获取用户银行卡列表
     */
    @GetMapping
    public ResponseEntity<List<BankCardDTO>> getUserBankCards(@RequestParam Long userId) {
        List<BankCardDTO> bankCards = bankCardService.getUserBankCards(userId);
        return ResponseEntity.ok(bankCards);
    }

    /**
     * 添加银行卡
     */
    @PostMapping
    public ResponseEntity<BankCardDTO> addBankCard(
            @RequestParam Long userId,
            @Valid @RequestBody BankCardCreateRequest request) {
        BankCardDTO bankCard = bankCardService.addBankCard(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bankCard);
    }

    /**
     * 删除银行卡
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankCard(
            @RequestParam Long userId,
            @PathVariable Long id) {
        bankCardService.deleteBankCard(userId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 设置默认银行卡
     */
    @PutMapping("/{id}/set-default")
    public ResponseEntity<BankCardDTO> setDefaultBankCard(
            @RequestParam Long userId,
            @PathVariable Long id) {
        BankCardDTO bankCard = bankCardService.setDefaultBankCard(userId, id);
        return ResponseEntity.ok(bankCard);
    }

    /**
     * 获取默认银行卡
     */
    @GetMapping("/default")
    public ResponseEntity<BankCardDTO> getDefaultBankCard(@RequestParam Long userId) {
        BankCardDTO bankCard = bankCardService.getDefaultBankCard(userId);
        if (bankCard == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bankCard);
    }
}
