package com.example.demo.pojo;

import lombok.Data;

@Data
public class RecommendRequest {
    private Integer userId;              // 用户ID（可选）
    private Integer artworkId;           // 基于某个作品推荐相似作品
    private String recommendType;        // 推荐类型：similar, hot, personalized
    private Integer count = 10;          // 推荐数量
}