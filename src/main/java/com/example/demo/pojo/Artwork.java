package com.example.demo.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Artwork {
    private Integer id;
    private String title;
    private String description;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer categoryId;
    private Integer userId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer status;// 状态：0-下架，1-上架可售，2-审核中，3-已售出，4-锁定中(预售)
    private Boolean isFeatured;
    private LocalDateTime uploadTime;
    private LocalDateTime updateTime;
    // 关联属性
    private String categoryName;  // 分类名称
    private String authorName;    // 作者名称

    // 添加定价相关字段
    private BigDecimal costPrice;         // 成本价格（内部使用）
    private Integer priceType;            // 价格类型：1-固定价格，2-起拍价（拍卖）
    private Boolean allowDiscount;        // 是否允许参与折扣活动

    // 便捷方法
    public boolean isAvailable() {
        return this.status != null && this.status == 1;
    }

    public boolean isSold() {
        return this.status != null && this.status == 3;
    }

    public boolean isLocked() {
        return this.status != null && this.status == 4;
    }
}