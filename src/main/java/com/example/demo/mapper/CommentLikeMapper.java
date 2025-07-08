package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentLikeMapper {

    // 点赞评论
    void insert(@Param("commentId") Integer commentId, @Param("userId") Integer userId);

    // 取消点赞
    void delete(@Param("commentId") Integer commentId, @Param("userId") Integer userId);

    // 检查是否已点赞
    boolean isLiked(@Param("commentId") Integer commentId, @Param("userId") Integer userId);

    // 批量查询用户点赞的评论
    List<Integer> getUserLikedComments(@Param("userId") Integer userId,
                                       @Param("commentIds") List<Integer> commentIds);
}