package com.example.demo.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SearchLog {
    private Integer id;
    private Integer userId;
    private String keyword;
    private Integer searchType;
    private Integer resultCount;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createTime;

    // 关联字段
    private String username;
}