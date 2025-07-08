package com.example.demo.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserStatistics {
    private Integer id;
    private Integer userId;
    private String statMonth;
    private Integer artworksUploaded;
    private Integer artworksViewed;
    private Integer artworksFavorited;
    private Integer commentsMade;
    private Integer searchesMade;
    private Integer loginDays;
    private BigDecimal totalRevenue;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段
    private String username;
}