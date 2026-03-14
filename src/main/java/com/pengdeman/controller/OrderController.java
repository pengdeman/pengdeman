package com.pengdeman.controller;

import com.pengdeman.dto.OrderCreateRequest;
import com.pengdeman.dto.OrderDTO;
import com.pengdeman.dto.PageResponse;
import com.pengdeman.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单管理API控制器
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     * 注意：实际使用时应该从token中解析userId，而不是从路径参数获取
     */
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @RequestParam Long userId,
            @Valid @RequestBody OrderCreateRequest request) {
        OrderDTO createdOrder = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * 获取用户订单列表（分页）
     */
    @GetMapping
    public ResponseEntity<PageResponse<OrderDTO>> getUserOrders(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderDTO> orders = orderService.getUserOrders(userId, status, page, size);
        return ResponseEntity.ok(orders);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @RequestParam Long userId,
            @PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(userId, id);
        return ResponseEntity.ok(order);
    }

    /**
     * 搜索用户订单（分页）
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<OrderDTO>> searchOrders(
            @RequestParam Long userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderDTO> orders = orderService.searchUserOrders(userId, keyword, page, size);
        return ResponseEntity.ok(orders);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(
            @RequestParam Long userId,
            @PathVariable Long id) {
        OrderDTO order = orderService.cancelOrder(userId, id);
        return ResponseEntity.ok(order);
    }

    /**
     * 确认收货
     */
    @PutMapping("/{id}/confirm-receipt")
    public ResponseEntity<OrderDTO> confirmReceipt(
            @RequestParam Long userId,
            @PathVariable Long id) {
        OrderDTO order = orderService.confirmReceipt(userId, id);
        return ResponseEntity.ok(order);
    }
}
