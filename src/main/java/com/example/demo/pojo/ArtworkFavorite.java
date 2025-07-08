package com.example.demo.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class ArtworkFavorite {
    private Integer id;
    private Integer userId;
    private Integer artworkId;
    private LocalDateTime createTime;

    // 关联查询字段（收藏列表时需要显示作品信息）
    private String artworkTitle;
    private String artworkImageUrl;
    private String artworkThumbnailUrl;
    private BigDecimal artworkPrice;
    private String authorName;
    private String categoryName;
    private Integer artworkStatus;
}