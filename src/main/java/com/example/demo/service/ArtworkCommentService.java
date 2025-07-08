package com.example.demo.service;

import com.example.demo.mapper.ArtworkCommentMapper;
import com.example.demo.mapper.CommentLikeMapper;
import com.example.demo.pojo.ArtworkComment;
import com.example.demo.pojo.CommentRequest;
import com.example.demo.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArtworkCommentService {

    @Autowired
    private ArtworkCommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Autowired
    private UserBehaviorService userBehaviorService;

    /**
     * 发表评论
     */
    @Transactional
    public void addComment(CommentRequest request) {
        Integer userId = UserContext.getCurrentUserId();

        // 验证评分范围
        if (request.getRating() != null && (request.getRating() < 1 || request.getRating() > 5)) {
            throw new RuntimeException("评分必须在1-5星之间");
        }

        // 只有顶级评论才能有评分
        if (request.getParentId() != null && request.getRating() != null) {
            throw new RuntimeException("回复评论不能设置评分");
        }

        ArtworkComment comment = new ArtworkComment();
        comment.setArtworkId(request.getArtworkId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent());
        comment.setRating(request.getRating());
        comment.setStatus(1);

        commentMapper.insert(comment);

        // 记录评论行为
        userBehaviorService.recordCommentBehavior(request.getArtworkId());

        // 如果是回复，更新父评论的回复数量
        if (request.getParentId() != null) {
            commentMapper.updateReplyCount(request.getParentId(), 1);
        }
    }

    /**
     * 获取作品评论列表（分页）
     */
    public List<ArtworkComment> getArtworkComments(Integer artworkId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ArtworkComment> comments = commentMapper.selectTopLevelComments(artworkId, offset, pageSize);

        // 为每个评论设置点赞状态和加载部分回复
        Integer currentUserId = getCurrentUserIdSafely();

        for (ArtworkComment comment : comments) {
            // 设置点赞状态
            if (currentUserId != null) {
                boolean isLiked = commentLikeMapper.isLiked(comment.getId(), currentUserId);
                comment.setIsLiked(isLiked);
            }

            // 加载前3条回复
            if (comment.getReplyCount() > 0) {
                List<ArtworkComment> replies = commentMapper.selectRepliesByParentId(comment.getId(), 0, 3);
                // 为回复也设置点赞状态
                if (currentUserId != null) {
                    for (ArtworkComment reply : replies) {
                        boolean isLiked = commentLikeMapper.isLiked(reply.getId(), currentUserId);
                        reply.setIsLiked(isLiked);
                    }
                }
                comment.setReplies(replies);
            }
        }

        return comments;
    }

    /**
     * 获取评论的回复列表
     */
    public List<ArtworkComment> getCommentReplies(Integer parentId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ArtworkComment> replies = commentMapper.selectRepliesByParentId(parentId, offset, pageSize);

        // 设置点赞状态
        Integer currentUserId = getCurrentUserIdSafely();
        if (currentUserId != null) {
            for (ArtworkComment reply : replies) {
                boolean isLiked = commentLikeMapper.isLiked(reply.getId(), currentUserId);
                reply.setIsLiked(isLiked);
            }
        }

        return replies;
    }

    /**
     * 点赞/取消点赞评论
     */
    @Transactional
    public boolean toggleCommentLike(Integer commentId) {
        Integer userId = UserContext.getCurrentUserId();

        boolean isLiked = commentLikeMapper.isLiked(commentId, userId);

        if (isLiked) {
            // 取消点赞
            commentLikeMapper.delete(commentId, userId);
            commentMapper.updateLikeCount(commentId, -1);
            return false;
        } else {
            // 点赞
            commentLikeMapper.insert(commentId, userId);
            commentMapper.updateLikeCount(commentId, 1);
            return true;
        }
    }

    /**
     * 删除评论
     */
    @Transactional
    public boolean deleteComment(Integer commentId) {
        Integer userId = UserContext.getCurrentUserId();

        ArtworkComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的评论");
        }

        // 软删除评论
        commentMapper.deleteById(commentId, userId);

        // 如果是回复，减少父评论的回复数
        if (comment.getParentId() != null) {
            commentMapper.updateReplyCount(comment.getParentId(), -1);
        }

        return true;
    }

    /**
     * 获取作品评论统计
     */
    public Map<String, Object> getCommentStats(Integer artworkId) {
        return commentMapper.getCommentStats(artworkId);
    }

    /**
     * 获取我的评论列表
     */
    public List<ArtworkComment> getMyComments(int page, int pageSize) {
        Integer userId = UserContext.getCurrentUserId();
        int offset = (page - 1) * pageSize;
        return commentMapper.selectByUserId(userId, offset, pageSize);
    }

    /**
     * 安全获取当前用户ID（未登录返回null）
     */
    private Integer getCurrentUserIdSafely() {
        try {
            return UserContext.getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}