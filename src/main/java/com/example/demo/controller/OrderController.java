package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.pojo.*;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Map<String, String>> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            String orderNo = orderService.createOrder(request);
            return Result.success("订单创建成功", Map.of("orderNo", orderNo));
        } catch (Exception e) {
            return Result.error("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderNo}")
    public Result<Order> getOrderDetail(@PathVariable String orderNo) {
        Order order = orderService.getOrderDetail(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    /**
     * 获取我的订单列表
     */
    @GetMapping("/my")
    public Result<List<Order>> getMyOrders(@RequestParam(required = false) Integer status,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        List<Order> orders = orderService.getMyOrders(status, page, pageSize);
        return Result.success(orders);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderNo}")
    public Result<String> payOrder(@PathVariable String orderNo) {
        try {
            boolean success = orderService.payOrder(orderNo);
            return success ? Result.success("支付成功") : Result.error("支付失败");
        } catch (Exception e) {
            return Result.error("支付失败：" + e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public Result<String> cancelOrder(@PathVariable String orderNo) {
        try {
            boolean success = orderService.cancelOrder(orderNo);
            return success ? Result.success("订单已取消") : Result.error("取消失败");
        } catch (Exception e) {
            return Result.error("取消失败：" + e.getMessage());
        }
    }

    /**
     * 完成订单
     */
    @PostMapping("/complete/{orderNo}")
    public Result<String> completeOrder(@PathVariable String orderNo) {
        try {
            boolean success = orderService.completeOrder(orderNo);
            return success ? Result.success("订单已完成") : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 获取销售记录
     */
    @GetMapping("/sales")
    public Result<List<OrderItem>> getSalesRecords(@RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        List<OrderItem> sales = orderService.getSalesRecords(status, page, pageSize);
        return Result.success(sales);
    }

    @GetMapping("/status/{orderNo}")
    public Result<Map<String, Object>> getOrderStatus(@PathVariable String orderNo) {
        Order order = orderService.getOrderByOrderNo(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("status", order.getStatus());
        result.put("totalAmount", order.getTotalAmount());

        return Result.success(result);
    }
}