package com.example.demo.pojo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArtworkComment {
    private Integer id;
    private Integer artworkId;
    private Integer userId;
    private Integer parentId;
    private String content;
    private Integer rating;
    private Integer likeCount;
    private Integer replyCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联查询字段
    private String username;  // 移除了userAvatar字段
    private Boolean isLiked;  // 当前用户是否已点赞
    private List<ArtworkComment> replies;  // 回复列表

    // 父评论信息（用于回复显示）
    private String parentUsername;

    // 用户评论列表时的额外字段
    private String artworkTitle;
    private String artworkImage;
}