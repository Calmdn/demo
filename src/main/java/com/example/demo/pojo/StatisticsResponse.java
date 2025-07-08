package com.example.demo.pojo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsResponse {
    private Map<String, Object> overview;           // 概览数据
    private List<Map<String, Object>> trends;       // 趋势数据
    private List<Map<String, Object>> rankings;     // 排行榜数据
    private List<Map<String, Object>> comparisons;  // 对比数据
    private Map<String, Object> metadata;          // 元数据（时间范围、总数等）
}