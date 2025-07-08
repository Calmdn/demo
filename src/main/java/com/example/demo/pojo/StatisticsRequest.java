package com.example.demo.pojo;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StatisticsRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String timeRange;     // today, week, month, quarter, year
    private String dimension;     // user, artwork, category, platform
    private Integer userId;       // 指定用户统计
    private Integer categoryId;   // 指定分类统计
    private String sortBy;        // 排序字段
    private String sortOrder;     // asc, desc
    private Integer page = 1;
    private Integer pageSize = 20;
}