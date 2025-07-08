package com.example.demo.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer artworkId;
    private String artworkTitle;
    private String artworkImageUrl;
    private Integer sellerId;
    private String sellerName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private LocalDateTime createTime;
}