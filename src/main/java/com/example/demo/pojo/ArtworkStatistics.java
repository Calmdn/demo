package com.example.demo.pojo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ArtworkStatistics {
    private Integer id;
    private Integer artworkId;
    private LocalDate statDate;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Integer shareCount;
    private LocalDateTime createTime;

    // 关联字段
    private String artworkTitle;
    private String authorName;
}