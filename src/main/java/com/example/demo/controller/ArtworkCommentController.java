package com.example.demo.controller;

import com.example.demo.pojo.ArtworkComment;
import com.example.demo.pojo.CommentRequest;
import com.example.demo.common.Result;
import com.example.demo.service.ArtworkCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class ArtworkCommentController {

    @Autowired
    private ArtworkCommentService commentService;

    /**
     * 发表评论
     */
    @PostMapping("/add")
    public Result<String> addComment(@RequestBody CommentRequest request) {
        try {
            commentService.addComment(request);
            return Result.success("评论发表成功");
        } catch (Exception e) {
            return Result.error("发表失败：" + e.getMessage());
        }
    }

    /**
     * 获取作品评论列表
     */
    @GetMapping("/artwork/{artworkId}")
    public Result<List<ArtworkComment>> getArtworkComments(@PathVariable Integer artworkId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int pageSize) {
        List<ArtworkComment> comments = commentService.getArtworkComments(artworkId, page, pageSize);
        return Result.success(comments);
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/replies/{parentId}")
    public Result<List<ArtworkComment>> getCommentReplies(@PathVariable Integer parentId,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int pageSize) {
        List<ArtworkComment> replies = commentService.getCommentReplies(parentId, page, pageSize);
        return Result.success(replies);
    }

    /**
     * 点赞/取消点赞评论
     */
    @PostMapping("/like/{commentId}")
    public Result<Map<String, Object>> toggleCommentLike(@PathVariable Integer commentId) {
        try {
            boolean isLiked = commentService.toggleCommentLike(commentId);
            return Result.success(Map.of(
                    "isLiked", isLiked,
                    "message", isLiked ? "点赞成功" : "取消点赞成功"
            ));
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<String> deleteComment(@PathVariable Integer commentId) {
        try {
            boolean success = commentService.deleteComment(commentId);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取作品评论统计
     */
    @GetMapping("/stats/{artworkId}")
    public Result<Map<String, Object>> getCommentStats(@PathVariable Integer artworkId) {
        Map<String, Object> stats = commentService.getCommentStats(artworkId);
        return Result.success(stats);
    }

    /**
     * 获取我的评论列表
     */
    @GetMapping("/my")
    public Result<List<ArtworkComment>> getMyComments(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        List<ArtworkComment> comments = commentService.getMyComments(page, pageSize);
        return Result.success(comments);
    }
}