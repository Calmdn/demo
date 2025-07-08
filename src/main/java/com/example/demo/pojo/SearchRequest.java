package com.example.demo.pojo;

import lombok.Data;
import java.util.List;

@Data
public class SearchRequest {
    private String keyword;              // 搜索关键词
    private Integer categoryId;          // 分类筛选
    private List<String> tags;           // 标签筛选
    private String priceRange;           // 价格范围：如 "0-100", "100-500"
    private String sortBy;               // 排序方式：price_asc, price_desc, time_desc, popular
    private Integer page = 1;            // 页码
    private Integer pageSize = 20;       // 每页数量
}