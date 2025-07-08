package com.example.demo.pojo;

import lombok.Data;

@Data
public class CommentRequest {
    private Integer artworkId;
    private Integer parentId;  // 回复评论时的父评论ID
    private String content;
    private Integer rating;    // 评分（1-5星，只有顶级评论才能评分）
}