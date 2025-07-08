package com.example.demo.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ArtworkDiscount {
    private Integer id;
    private Integer artworkId;
    private Integer discountType;
    private BigDecimal discountValue;
    private BigDecimal minPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private String description;
    private Integer creatorId;
    private LocalDateTime createTime;

    // 关联查询字段
    private String artworkTitle;
    private String creatorName;

    // 计算字段
    private Boolean isActive;  // 是否当前生效
    private BigDecimal calculatedPrice;  // 计算后的价格
}