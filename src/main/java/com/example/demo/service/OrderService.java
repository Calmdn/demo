package com.example.demo.service;

import com.example.demo.mapper.ArtworkMapper;
import com.example.demo.mapper.OrderItemMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.pojo.*;
import com.example.demo.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private DistributedLockService distributedLockService;

    @Autowired
    private MessageProducerService messageProducer;

    // 订单号生成器
    private static final AtomicLong orderCounter = new AtomicLong(1);

    /**
     * 创建订单
     */
    @Transactional
    public String createOrder(CreateOrderRequest request) {
        Integer userId = UserContext.getCurrentUserId();

        // 为整个订单创建过程加锁（防止重复提交）
        String orderLockKey = "create_order_lock:user:" + userId;

        return distributedLockService.executeWithLock(orderLockKey, () -> {

            // 1. 验证作品并立即锁定
            for (Integer artworkId : request.getArtworkIds()) {
                String artworkLockKey = "artwork_purchase_lock:" + artworkId;

                distributedLockService.executeWithLock(artworkLockKey, () -> {
                    Artwork artwork = artworkMapper.selectById(artworkId);
                    if (artwork == null) {
                        throw new RuntimeException("作品ID " + artworkId + " 不存在");
                    }
                    // 使用 status 字段检查
                    if (!artwork.isAvailable()) {
                        throw new RuntimeException("作品《" + artwork.getTitle() + "》不可购买，当前状态：" + getStatusName(artwork.getStatus()));
                    }
                    if (artwork.getUserId().equals(userId)) {
                        throw new RuntimeException("不能购买自己的作品");
                    }

                    // 关键修改：立即将作品状态改为锁定中，防止其他人购买
                    int updateResult = artworkMapper.lockArtworkForPurchase(artworkId);
                    if (updateResult == 0) {
                        // 说明在我们检查和锁定之间，作品已被其他人锁定
                        throw new RuntimeException("作品《" + artwork.getTitle() + "》已被其他用户抢购");
                    }

                    return null;
                });
            }

            // 2. 创建基础订单
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setStatus(0); // 0-创建中
            order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "alipay");
            order.setRemark(request.getRemark());
            order.setTotalAmount(BigDecimal.ZERO);
            order.setOriginalAmount(BigDecimal.ZERO);
            order.setDiscountAmount(BigDecimal.ZERO);

            orderMapper.insert(order);

            // 3. 发送异步处理消息
            messageProducer.sendOrderProcessMessage(order.getId(), request);
            messageProducer.sendDelayOrderCheckMessage(order.getId());

            return order.getOrderNo();
        });
    }

    /**
     * 获取订单详情
     */
    public Order getOrderDetail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            // 获取订单项
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
            order.setOrderItems(orderItems);
        }
        return order;
    }

    /**
     * 获取我的订单列表
     */
    public List<Order> getMyOrders(Integer status, int page, int pageSize) {
        Integer userId = UserContext.getCurrentUserId();
        int offset = (page - 1) * pageSize;

        List<Order> orders = orderMapper.selectByUserId(userId, status, offset, pageSize);

        // 为每个订单加载订单项
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
            order.setOrderItems(orderItems);
        }

        return orders;
    }

    /**
     * 支付订单（模拟支付）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        Integer userId = UserContext.getCurrentUserId();
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此订单");
        }

        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许支付");
        }

        // 标记作品为已售出（status 4->3，锁定中->已售出）
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            int result = artworkMapper.markAsSold(item.getArtworkId()); // 修改方法名
            if (result != 1) {
                throw new RuntimeException("作品状态更新失败");
            }
        }

        // 更新订单状态为已支付
        orderMapper.updateStatus(order.getId(), 2);

        return true;
    }

    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        Integer userId = UserContext.getCurrentUserId();
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此订单");
        }

        if (order.getStatus() != 1) {
            throw new RuntimeException("只能取消待支付订单");
        }

        // 释放作品状态（从锁定状态恢复到上架状态）
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            artworkMapper.rollbackToAvailable(item.getArtworkId());
        }

        orderMapper.updateStatus(order.getId(), 4); // 已取消

        return true;
    }

    /**
     * 完成订单
     */
    @Transactional
    public boolean completeOrder(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        Integer userId = UserContext.getCurrentUserId();
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此订单");
        }

        if (order.getStatus() != 2) {
            throw new RuntimeException("只能完成已支付订单");
        }

        orderMapper.updateStatus(order.getId(), 3); // 已完成

        return true;
    }

    /**
     * 获取销售记录
     */
    public List<OrderItem> getSalesRecords(Integer status, int page, int pageSize) {
        Integer userId = UserContext.getCurrentUserId();
        int offset = (page - 1) * pageSize;
        return orderItemMapper.selectBySellerId(userId, status, offset, pageSize);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long counter = orderCounter.getAndIncrement();
        return "OD" + timestamp + String.format("%04d", counter % 10000);
    }

    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }
    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "下架";
            case 1: return "上架可售";
            case 2: return "审核中";
            case 3: return "已售出";
            case 4: return "锁定中";
            default: return "未知状态";
        }
    }

}