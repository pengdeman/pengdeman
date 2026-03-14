package com.pengdeman.service;

import com.pengdeman.dto.OrderCreateRequest;
import com.pengdeman.dto.OrderDTO;
import com.pengdeman.dto.PageResponse;
import com.pengdeman.exception.OrderNotFoundException;
import com.pengdeman.exception.UserFinanceException;
import com.pengdeman.model.*;
import com.pengdeman.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单管理服务
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserFinanceRepository userFinanceRepository;
    private final CommissionRecordRepository commissionRecordRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserFinanceRepository userFinanceRepository,
                        CommissionRecordRepository commissionRecordRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userFinanceRepository = userFinanceRepository;
        this.commissionRecordRepository = commissionRecordRepository;
    }

    /**
     * 创建订单
     */
    @Transactional
    public OrderDTO createOrder(Long userId, OrderCreateRequest request) {
        // 1. 生成订单号
        String orderNo = generateOrderNo();

        // 2. 创建订单实体
        OrderEntity order = new OrderEntity();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setSku(request.getSku());
        order.setTitle(request.getTitle());
        order.setPrice(request.getPrice());
        order.setCommission(request.getCommission());
        order.setUserCommission(request.getUserCommission());
        order.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        order.setTotalAmount(request.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        order.setPromotionLink(request.getPromotionLink());
        order.setProductImage(request.getProductImage());
        order.setStatus(1); // 待支付
        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderType(request.getOrderType());

        // 查找或创建商品信息
        ProductEntity product = productRepository.findBySku(request.getSku()).orElseGet(() -> {
            ProductEntity newProduct = new ProductEntity();
            newProduct.setSku(request.getSku());
            newProduct.setTitle(request.getTitle());
            newProduct.setPrice(request.getPrice());
            newProduct.setCommissionRate(request.getCommissionRate());
            newProduct.setImageUrl(request.getProductImage());
            newProduct.setStatus(1);
            return productRepository.save(newProduct);
        });
        order.setProductId(product.getId());

        // 保存订单
        OrderEntity savedOrder = orderRepository.save(order);

        // 如果订单状态是已支付，则更新用户资金和创建佣金记录
        if (savedOrder.getStatus() == 2) {
            processPaidOrder(savedOrder);
        }

        return convertToDTO(savedOrder);
    }

    /**
     * 获取用户订单列表（分页）
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderDTO> getUserOrders(Long userId, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderEntity> orderPage;

        if (status != null && status > 0) {
            orderPage = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            orderPage = orderRepository.findByUserId(userId, pageable);
        }

        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (OrderEntity entity : orderPage.getContent()) {
            orderDTOs.add(convertToDTO(entity));
        }

        return new PageResponse<>(
                orderDTOs,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
    }

    /**
     * 根据ID获取订单详情
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long userId, Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 验证是否属于该用户
        if (!order.getUserId().equals(userId)) {
            throw new OrderNotFoundException(orderId);
        }

        return convertToDTO(order);
    }

    /**
     * 搜索用户订单（分页）
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderDTO> searchUserOrders(Long userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderEntity> orderPage = orderRepository.findByUserIdAndTitleContaining(userId, keyword, pageable);

        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (OrderEntity entity : orderPage.getContent()) {
            orderDTOs.add(convertToDTO(entity));
        }

        return new PageResponse<>(
                orderDTOs,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
    }

    /**
     * 取消订单
     */
    public OrderDTO cancelOrder(Long userId, Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 验证是否属于该用户
        if (!order.getUserId().equals(userId)) {
            throw new OrderNotFoundException(orderId);
        }

        // 只有待支付状态的订单可以取消
        if (order.getStatus() != 1) {
            throw new IllegalStateException("订单状态不允许取消");
        }

        order.setStatus(5); // 已取消
        order.setCancelTime(LocalDateTime.now());

        OrderEntity updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    /**
     * 确认收货
     */
    public OrderDTO confirmReceipt(Long userId, Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 验证是否属于该用户
        if (!order.getUserId().equals(userId)) {
            throw new OrderNotFoundException(orderId);
        }

        // 只有已发货状态的订单可以确认收货
        if (order.getStatus() != 3) {
            throw new IllegalStateException("订单状态不允许确认收货");
        }

        order.setStatus(4); // 已收货
        order.setReceiptTime(LocalDateTime.now());

        OrderEntity updatedOrder = orderRepository.save(order);

        // 处理已完成订单的佣金结算
        processCompletedOrder(updatedOrder);

        return convertToDTO(updatedOrder);
    }

    /**
     * 更新订单状态（供内部调用）
     */
    public OrderDTO updateOrderStatus(Long orderId, Integer status) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(status);

        switch (status) {
            case 2: // 已支付
                order.setPaymentTime(LocalDateTime.now());
                processPaidOrder(order);
                break;
            case 3: // 已发货
                order.setShippingTime(LocalDateTime.now());
                break;
            case 4: // 已收货
                order.setReceiptTime(LocalDateTime.now());
                processCompletedOrder(order);
                break;
            case 5: // 已取消
                order.setCancelTime(LocalDateTime.now());
                break;
        }

        OrderEntity updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    /**
     * 处理已支付订单
     */
    private void processPaidOrder(OrderEntity order) {
        // 更新用户资金信息 - 增加总收入和订单数
        UserFinanceEntity finance = userFinanceRepository.findByUserId(order.getUserId())
                .orElseGet(() -> {
                    UserFinanceEntity newFinance = new UserFinanceEntity();
                    newFinance.setUserId(order.getUserId());
                    newFinance.setBalance(BigDecimal.ZERO);
                    newFinance.setTotalIncome(BigDecimal.ZERO);
                    newFinance.setWithdrawableAmount(BigDecimal.ZERO);
                    newFinance.setOrderCount(0);
                    newFinance.setPendingWithdrawal(BigDecimal.ZERO);
                    newFinance.setTotalWithdrawn(BigDecimal.ZERO);
                    return newFinance;
                });

        finance.setOrderCount(finance.getOrderCount() + 1);
        userFinanceRepository.save(finance);
    }

    /**
     * 处理已完成订单 - 结算佣金
     */
    private void processCompletedOrder(OrderEntity order) {
        // 1. 创建佣金记录
        CommissionRecordEntity commission = new CommissionRecordEntity();
        commission.setUserId(order.getUserId());
        commission.setOrderId(order.getId());
        commission.setProductId(order.getProductId());
        commission.setCommissionAmount(order.getUserCommission());
        commission.setStatus(2); // 已结算
        commission.setSettledTime(LocalDateTime.now());
        commissionRecordRepository.save(commission);

        // 2. 更新用户资金
        UserFinanceEntity finance = userFinanceRepository.findByUserId(order.getUserId())
                .orElseThrow(() -> new UserFinanceException("用户资金信息不存在"));

        // 增加余额、总收入、可提现金额
        BigDecimal userCommission = order.getUserCommission();
        finance.setBalance(finance.getBalance().add(userCommission));
        finance.setTotalIncome(finance.getTotalIncome().add(userCommission));
        // 假设可提现金额为余额的80%
        finance.setWithdrawableAmount(finance.getBalance().multiply(BigDecimal.valueOf(0.8)).setScale(2, BigDecimal.ROUND_HALF_UP));

        userFinanceRepository.save(finance);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "JD" + System.currentTimeMillis();
    }

    /**
     * 转换OrderEntity到OrderDTO
     */
    private OrderDTO convertToDTO(OrderEntity entity) {
        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());
        dto.setOrderNo(entity.getOrderNo());
        dto.setUserId(entity.getUserId());
        dto.setProductId(entity.getProductId());
        dto.setSku(entity.getSku());
        dto.setTitle(entity.getTitle());
        dto.setPrice(entity.getPrice());
        dto.setCommission(entity.getCommission());
        dto.setUserCommission(entity.getUserCommission());
        dto.setQuantity(entity.getQuantity());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPromotionLink(entity.getPromotionLink());
        dto.setProductImage(entity.getProductImage());
        dto.setStatus(entity.getStatus());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setPaymentTime(entity.getPaymentTime());
        dto.setShippingTime(entity.getShippingTime());
        dto.setReceiptTime(entity.getReceiptTime());
        dto.setCancelTime(entity.getCancelTime());
        dto.setOrderType(entity.getOrderType());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}
