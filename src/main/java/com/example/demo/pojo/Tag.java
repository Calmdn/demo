package com.example.demo.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Tag {
    private Integer id;
    private String name;
    private Integer usageCount;
    private Integer status;
    private LocalDateTime createTime;
}