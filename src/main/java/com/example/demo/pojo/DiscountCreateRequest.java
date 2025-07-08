package com.example.demo.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountCreateRequest {
    private Integer artworkId;
    private Integer discountType;  // 1-百分比折扣，2-固定金额减免，3-限时特价
    private BigDecimal discountValue;
    private BigDecimal minPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
}