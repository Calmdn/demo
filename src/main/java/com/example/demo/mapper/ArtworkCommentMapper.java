package com.example.demo.mapper;

import com.example.demo.pojo.ArtworkComment;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArtworkCommentMapper {

    // 插入评论
    void insert(ArtworkComment comment);

    // 查询作品的顶级评论列表
    List<ArtworkComment> selectTopLevelComments(@Param("artworkId") Integer artworkId,
                                                @Param("offset") int offset,
                                                @Param("pageSize") int pageSize);

    // 查询评论的回复列表
    List<ArtworkComment> selectRepliesByParentId(@Param("parentId") Integer parentId,
                                                 @Param("offset") int offset,
                                                 @Param("pageSize") int pageSize);

    // 根据ID查询评论
    ArtworkComment selectById(Integer id);

    // 更新回复数量
    void updateReplyCount(@Param("commentId") Integer commentId, @Param("increment") int increment);

    // 更新点赞数量
    void updateLikeCount(@Param("commentId") Integer commentId, @Param("increment") int increment);

    // 删除评论
    void deleteById(@Param("id") Integer id, @Param("userId") Integer userId);

    // 获取作品评论统计
    @MapKey("commentCount")
    Map<String, Object> getCommentStats(Integer artworkId);

    // 查询用户的评论列表
    List<ArtworkComment> selectByUserId(@Param("userId") Integer userId,
                                        @Param("offset") int offset,
                                        @Param("pageSize") int pageSize);
}