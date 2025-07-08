package com.example.demo.service;

import com.example.demo.config.RabbitConfig;
import com.example.demo.pojo.CreateOrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 发送订单处理消息
     */
    public void sendOrderMessage(Integer orderId, Integer userId, String orderData) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", orderId);
            message.put("userId", userId);
            message.put("orderData", orderData);
            message.put("timestamp", System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.ORDER_EXCHANGE,
                    RabbitConfig.ORDER_ROUTING_KEY,
                    message
            );

            System.out.println("订单消息已发送到队列：" + orderId);
        } catch (Exception e) {
            System.err.println("发送订单消息失败：" + e.getMessage());
        }
    }

    /**
     * 发送库存更新消息
     */
    public void sendStockUpdateMessage(Integer artworkId, Integer quantity, String operation) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("artworkId", artworkId);
            message.put("quantity", quantity);
            message.put("operation", operation); // "reduce" 或 "restore"
            message.put("timestamp", System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.STOCK_EXCHANGE,
                    RabbitConfig.STOCK_ROUTING_KEY,
                    message
            );

            System.out.println("库存更新消息已发送：" + artworkId + " " + operation + " " + quantity);
        } catch (Exception e) {
            System.err.println("发送库存更新消息失败：" + e.getMessage());
        }
    }

    /**
     * 发送订单处理消息
     */
    public void sendOrderProcessMessage(Integer orderId, CreateOrderRequest request) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", orderId);
            message.put("artworkIds", request.getArtworkIds());
            message.put("paymentMethod", request.getPaymentMethod());
            message.put("remark", request.getRemark());
            message.put("timestamp", System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.ORDER_EXCHANGE,
                    RabbitConfig.ORDER_ROUTING_KEY,
                    message
            );

            System.out.println("订单处理消息已发送：" + orderId);
        } catch (Exception e) {
            System.err.println("发送订单处理消息失败：" + e.getMessage());
        }
    }

    /**
     * 发送延时订单检查消息（用于订单超时处理）
     */
    public void sendDelayOrderCheckMessage(Integer orderId) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", orderId);
            message.put("checkType", "timeout");
            message.put("timestamp", System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.ORDER_DELAY_EXCHANGE,
                    RabbitConfig.ORDER_DELAY_ROUTING_KEY,
                    message
            );

            System.out.println("延时订单检查消息已发送：" + orderId);
        } catch (Exception e) {
            System.err.println("发送延时检查消息失败：" + e.getMessage());
        }
    }
}