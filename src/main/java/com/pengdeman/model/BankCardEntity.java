package com.pengdeman.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 银行卡实体类 - 存储用户银行卡信息
 */
@Entity
@Table(name = "bank_cards")
public class BankCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 银行名称
     */
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    /**
     * 银行卡号
     */
    @Column(name = "card_number", nullable = false, length = 30)
    private String cardNumber;

    /**
     * 持卡人姓名
     */
    @Column(name = "cardholder_name", nullable = false, length = 50)
    private String cardholderName;

    /**
     * 预留手机号
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * 是否默认
     */
    @Column(name = "is_default", nullable = false)
    private Integer isDefault = 0;

    /**
     * 银行卡类型：储蓄卡、信用卡
     */
    @Column(name = "card_type", length = 20)
    private String cardType;

    /**
     * 银行图标
     */
    @Column(name = "bank_icon", length = 50)
    private String bankIcon;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BankCardEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getBankIcon() {
        return bankIcon;
    }

    public void setBankIcon(String bankIcon) {
        this.bankIcon = bankIcon;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
