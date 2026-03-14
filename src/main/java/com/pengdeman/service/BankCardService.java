package com.pengdeman.service;

import com.pengdeman.dto.BankCardDTO;
import com.pengdeman.dto.BankCardCreateRequest;
import com.pengdeman.exception.BankCardException;
import com.pengdeman.model.BankCardEntity;
import com.pengdeman.repository.BankCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 银行卡管理服务
 */
@Service
@Transactional
public class BankCardService {

    private final BankCardRepository bankCardRepository;

    public BankCardService(BankCardRepository bankCardRepository) {
        this.bankCardRepository = bankCardRepository;
    }

    /**
     * 获取用户银行卡列表
     */
    @Transactional(readOnly = true)
    public List<BankCardDTO> getUserBankCards(Long userId) {
        List<BankCardEntity> bankCards = bankCardRepository.findByUserId(userId);
        List<BankCardDTO> dtos = new ArrayList<>();

        for (BankCardEntity entity : bankCards) {
            dtos.add(convertToDTO(entity));
        }

        return dtos;
    }

    /**
     * 添加银行卡
     */
    @Transactional
    public BankCardDTO addBankCard(Long userId, BankCardCreateRequest request) {
        // 验证银行卡是否已存在
        if (bankCardRepository.existsByUserIdAndCardNumber(userId, request.getCardNumber())) {
            throw new BankCardException("该银行卡已添加");
        }

        // 如果是默认银行卡，需要将其他银行卡设为非默认
        if (request.getIsDefault() == 1) {
            bankCardRepository.findByUserIdAndIsDefault(userId, 1).ifPresent(card -> {
                card.setIsDefault(0);
                bankCardRepository.save(card);
            });
        }

        BankCardEntity bankCard = new BankCardEntity();
        bankCard.setUserId(userId);
        bankCard.setBankName(request.getBankName());
        bankCard.setCardNumber(request.getCardNumber());
        bankCard.setCardholderName(request.getCardholderName());
        bankCard.setPhoneNumber(request.getPhoneNumber());
        bankCard.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);
        bankCard.setCardType(request.getCardType());
        bankCard.setBankIcon(request.getBankIcon());

        BankCardEntity savedBankCard = bankCardRepository.save(bankCard);
        return convertToDTO(savedBankCard);
    }

    /**
     * 删除银行卡
     */
    public void deleteBankCard(Long userId, Long bankCardId) {
        BankCardEntity bankCard = bankCardRepository.findById(bankCardId)
                .orElseThrow(() -> new BankCardException("银行卡不存在"));

        if (!bankCard.getUserId().equals(userId)) {
            throw new BankCardException("银行卡不属于该用户");
        }

        // 如果是默认银行卡，需要设置另一个银行卡为默认
        if (bankCard.getIsDefault() == 1) {
            List<BankCardEntity> otherCards = bankCardRepository.findByUserId(userId);
            if (otherCards.size() > 1) {
                // 找到第一个不是当前要删除的银行卡并设为默认
                for (BankCardEntity card : otherCards) {
                    if (!card.getId().equals(bankCardId)) {
                        card.setIsDefault(1);
                        bankCardRepository.save(card);
                        break;
                    }
                }
            }
        }

        bankCardRepository.delete(bankCard);
    }

    /**
     * 设置默认银行卡
     */
    public BankCardDTO setDefaultBankCard(Long userId, Long bankCardId) {
        BankCardEntity bankCard = bankCardRepository.findById(bankCardId)
                .orElseThrow(() -> new BankCardException("银行卡不存在"));

        if (!bankCard.getUserId().equals(userId)) {
            throw new BankCardException("银行卡不属于该用户");
        }

        // 将其他银行卡设为非默认
        bankCardRepository.findByUserIdAndIsDefault(userId, 1).ifPresent(card -> {
            card.setIsDefault(0);
            bankCardRepository.save(card);
        });

        bankCard.setIsDefault(1);
        BankCardEntity updatedBankCard = bankCardRepository.save(bankCard);

        return convertToDTO(updatedBankCard);
    }

    /**
     * 获取默认银行卡
     */
    @Transactional(readOnly = true)
    public BankCardDTO getDefaultBankCard(Long userId) {
        BankCardEntity bankCard = bankCardRepository.findByUserIdAndIsDefault(userId, 1).orElse(null);
        return bankCard != null ? convertToDTO(bankCard) : null;
    }

    /**
     * 转换BankCardEntity到BankCardDTO
     */
    private BankCardDTO convertToDTO(BankCardEntity entity) {
        BankCardDTO dto = new BankCardDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setBankName(entity.getBankName());
        dto.setCardNumber(entity.getCardNumber());
        dto.setCardNumberMasked(maskCardNumber(entity.getCardNumber()));
        dto.setCardholderName(entity.getCardholderName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setIsDefault(entity.getIsDefault());
        dto.setCardType(entity.getCardType());
        dto.setBankIcon(entity.getBankIcon());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    /**
     * 银行卡号脱敏
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return cardNumber;
        }

        int length = cardNumber.length();
        String prefix = cardNumber.substring(0, 4);
        String suffix = cardNumber.substring(length - 4);

        return prefix + " **** **** " + suffix;
    }
}
