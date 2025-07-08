package com.example.demo.service;

import com.example.demo.config.RabbitConfig;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.OrderItemMapper;
import com.example.demo.mapper.ArtworkMapper;
import com.example.demo.pojo.Order;
import com.example.demo.pojo.OrderItem;
import com.example.demo.pojo.Artwork;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MessageConsumerService {

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

    /**
     * 处理订单消息
     */
    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    @Transactional
    public void processOrder(Map<String, Object> message) {
        Integer orderId = null;
        List<Integer> artworkIds = null;

        try {
            orderId = (Integer) message.get("orderId");
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) message.get("artworkIds");
            artworkIds = ids;

            System.out.println("开始处理订单：" + orderId);

            // 为整个订单处理加锁，防止重复处理
            String orderProcessLockKey = "process_order_lock:" + orderId;

            Integer finalOrderId = orderId;
            List<Integer> finalArtworkIds = artworkIds;
            distributedLockService.executeWithLock(orderProcessLockKey, () -> {

                Order order = orderMapper.selectById(finalOrderId);
                if (order == null) {
                    System.err.println("订单不存在：" + finalOrderId);
                    return null;
                }

                // 检查订单是否已经处理过
                if (order.getStatus() != 0) {
                    System.out.println("订单已处理，跳过：" + finalOrderId + "，状态：" + order.getStatus());
                    return null;
                }

                // 验证作品状态（此时应该都是锁定状态3）
                List<Artwork> artworks = new ArrayList<>();
                for (Integer artworkId : finalArtworkIds) {
                    Artwork artwork = artworkMapper.selectById(artworkId);
                    if (artwork == null) {
                        throw new RuntimeException("作品不存在：" + artworkId);
                    }

                    // 检查作品是否处于锁定状态
                    if (artwork.getStatus() != 4) {
                        throw new RuntimeException("作品《" + artwork.getTitle() + "》状态异常，当前状态：" + getStatusName(artwork.getStatus()));
                    }

                    artworks.add(artwork);
                }

                // 批量计算价格
                Map<Integer, Map<String, Object>> priceInfoMap = pricingService.batchCalculatePrice(finalArtworkIds);

                BigDecimal totalAmount = BigDecimal.ZERO;
                BigDecimal originalAmount = BigDecimal.ZERO;

                for (Artwork artwork : artworks) {
                    Map<String, Object> priceInfo = priceInfoMap.get(artwork.getId());
                    if (priceInfo != null) {
                        BigDecimal finalPrice = (BigDecimal) priceInfo.get("finalPrice");
                        BigDecimal originalPrice = (BigDecimal) priceInfo.get("originalPrice");

                        totalAmount = totalAmount.add(finalPrice);
                        originalAmount = originalAmount.add(originalPrice);
                    } else {
                        totalAmount = totalAmount.add(artwork.getPrice());
                        originalAmount = originalAmount.add(artwork.getPrice());
                    }
                }

                BigDecimal discountAmount = originalAmount.subtract(totalAmount);

                // 更新订单金额
                order.setTotalAmount(totalAmount);
                order.setOriginalAmount(originalAmount);
                order.setDiscountAmount(discountAmount);
                order.setStatus(1); // 待支付
                orderMapper.update(order);

                // 创建订单项
                List<OrderItem> orderItems = new ArrayList<>();
                for (Artwork artwork : artworks) {
                    OrderItem item = new OrderItem();
                    item.setOrderId(order.getId());
                    item.setArtworkId(artwork.getId());
                    item.setArtworkTitle(artwork.getTitle());
                    item.setArtworkImageUrl(artwork.getThumbnailUrl() != null ?
                            artwork.getThumbnailUrl() : artwork.getImageUrl());
                    item.setSellerId(artwork.getUserId());
                    item.setPrice(artwork.getPrice());
                    item.setOriginalPrice(artwork.getOriginalPrice() != null ?
                            artwork.getOriginalPrice() : artwork.getPrice());
                    item.setSellerName(artwork.getAuthorName());
                    orderItems.add(item);
                }

                orderItemMapper.batchInsert(orderItems);

                System.out.println("订单处理完成：" + finalOrderId);
                return null;
            });

        } catch (Exception e) {
            System.err.println("处理订单消息失败：" + e.getMessage());
            // 失败处理：回滚作品状态和订单状态
            handleOrderProcessFailure(orderId, artworkIds, e);
        }
    }

    /**
     * 处理订单处理失败的情况
     */
    private void handleOrderProcessFailure(Integer orderId, List<Integer> artworkIds, Exception e) {
        try {
            // 1. 回滚作品状态（从锁定状态4回到上架状态1）
            if (artworkIds != null) {
                for (Integer artworkId : artworkIds) {
                    int rollbackResult = artworkMapper.rollbackArtworkStatus(artworkId);
                    if (rollbackResult > 0) {
                        System.out.println("回滚作品状态成功：" + artworkId + " (4->1)");
                    } else {
                        System.err.println("回滚作品状态失败：" + artworkId);
                    }
                }
            }

            // 2. 更新订单状态为失败
            if (orderId != null) {
                Order order = orderMapper.selectById(orderId);
                if (order != null && order.getStatus() == 0) {
                    order.setStatus(-1); // -1表示处理失败
                    orderMapper.update(order);
                    System.out.println("订单状态已更新为失败：" + orderId);
                }
            }

        } catch (Exception rollbackE) {
            System.err.println("回滚操作失败：" + rollbackE.getMessage());
        }
    }


    /**
     * 处理订单超时检查
     */
    @RabbitListener(queues = RabbitConfig.ORDER_DELAY_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void checkOrderTimeout(Map<String, Object> message) {
        try {
            Integer orderId = (Integer) message.get("orderId");

            System.out.println("检查订单超时：" + orderId);

            Order order = orderMapper.selectById(orderId);
            if (order != null && order.getStatus() == 1) { // 待支付状态
                order.setStatus(5); // 超时取消
                orderMapper.update(order);

                //释放作品状态（从锁定恢复到上架）
                List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
                for (OrderItem item : orderItems) {
                    artworkMapper.rollbackToAvailable(item.getArtworkId());
                }

                System.out.println("订单已超时取消：" + orderId);
            }

        } catch (Exception e) {
            System.err.println("检查订单超时失败：" + e.getMessage());
            throw e;
        }
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