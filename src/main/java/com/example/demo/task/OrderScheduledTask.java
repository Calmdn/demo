package com.example.demo.task;

import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.OrderItemMapper;
import com.example.demo.mapper.ArtworkMapper;
import com.example.demo.pojo.Order;
import com.example.demo.pojo.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderScheduledTask {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ArtworkMapper artworkMapper;

    /**
     * 定时扫描超时订单（作为RabbitMQ延时队列的兜底机制）
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    @Transactional(rollbackFor = Exception.class)
    public void scanTimeoutOrders() {
        try {
            System.out.println("开始扫描超时订单：" + LocalDateTime.now());

            // 查询创建超过30分钟且仍为待支付状态的订单
            List<Order> timeoutOrders = orderMapper.selectTimeoutOrders();

            if (timeoutOrders.isEmpty()) {
                System.out.println("没有发现超时订单");
                return;
            }

            System.out.println("发现 " + timeoutOrders.size() + " 个超时订单");

            for (Order order : timeoutOrders) {
                try {
                    // 更新订单状态为超时取消
                    orderMapper.updateStatus(order.getId(), 5); // 5-超时取消

                    // 释放作品状态（从锁定恢复到上架）
                    List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
                    for (OrderItem item : orderItems) {
                        int rollbackResult = artworkMapper.rollbackToAvailable(item.getArtworkId());
                        if (rollbackResult > 0) {
                            System.out.println("释放作品库存：" + item.getArtworkId());
                        }
                    }

                    System.out.println("超时订单处理完成：" + order.getOrderNo());

                } catch (Exception e) {
                    System.err.println("处理超时订单失败：" + order.getOrderNo() + "，错误：" + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("扫描超时订单任务失败：" + e.getMessage());
        }
    }

    /**
     * 定时清理过期的分布式锁（可选）
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000) // 1小时 = 3600000毫秒
    public void cleanExpiredLocks() {
        try {
            System.out.println("开始清理过期锁：" + LocalDateTime.now());
            // 这里可以添加清理Redis中过期锁的逻辑
            // 一般Redisson会自动处理，这里主要是监控

        } catch (Exception e) {
            System.err.println("清理过期锁失败：" + e.getMessage());
        }
    }
}