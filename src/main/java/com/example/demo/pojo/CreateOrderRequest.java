package com.example.demo.pojo;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private List<Integer> artworkIds;  // 要购买的作品ID列表
    private String paymentMethod;      // 支付方式：alipay, wechat, balance
    private String remark;             // 订单备注
}