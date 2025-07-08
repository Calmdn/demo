package com.example.demo.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserBehavior {
    private Integer id;
    private Integer userId;
    private Integer targetType;
    private Integer targetId;
    private Integer behaviorType;
    private Integer duration;
    private LocalDateTime createTime;

    // 关联字段
    private String username;
    private String targetTitle;  // 目标对象标题
}