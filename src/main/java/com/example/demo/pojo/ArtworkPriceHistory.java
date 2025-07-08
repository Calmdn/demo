package com.example.demo.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ArtworkPriceHistory {
    private Integer id;
    private Integer artworkId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private String changeReason;
    private Integer operatorId;
    private LocalDateTime createTime;

    // 关联查询字段
    private String artworkTitle;
    private String operatorName;
}