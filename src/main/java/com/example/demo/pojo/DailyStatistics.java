package com.example.demo.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyStatistics {
    private Integer id;
    private LocalDate statDate;
    private Integer newUsers;
    private Integer activeUsers;
    private Integer newArtworks;
    private Integer totalViews;
    private Integer totalFavorites;
    private Integer totalComments;
    private Integer totalSearches;
    private BigDecimal revenue;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}