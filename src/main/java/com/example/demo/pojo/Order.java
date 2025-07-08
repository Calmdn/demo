package com.example.demo.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private BigDecimal totalAmount;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private Integer status;
    private String paymentMethod;
    private LocalDateTime paymentTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联查询字段
    private String buyerName;
    private List<OrderItem> orderItems;
}