package com.example.demo.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArtworkCategory {
    private Integer id;
    private String name;
    private String description;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}