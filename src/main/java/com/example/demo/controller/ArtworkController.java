package com.example.demo.controller;

import com.example.demo.pojo.Artwork;
import com.example.demo.pojo.ArtworkCategory;
import com.example.demo.common.Result;
import com.example.demo.pojo.ArtworkComment;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artwork")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;
    @Autowired
    private ArtworkCategoryService categoryService;
    @Autowired
    private ArtworkFavoriteService favoriteService;
    @Autowired
    private ArtworkCommentService commentService;
    @Autowired
    private UserBehaviorService userBehaviorService;

    /**
     * 获取分类列表
     */
    @GetMapping("/categories")
    public Result<List<ArtworkCategory>> getCategories() {
        List<ArtworkCategory> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 发布作品
     */
    @PostMapping("/publish")
    public Result<String> publishArtwork(@RequestBody Artwork artwork) {
        try {
            artworkService.publishArtwork(artwork);
            return Result.success("作品发布成功");
        } catch (Exception e) {
            return Result.error("发布失败：" + e.getMessage());
        }
    }

    /**
     * 获取作品列表（分页）
     */
    @GetMapping("/list")
    public Result<List<Artwork>> getArtworkList(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "12") int pageSize) {
        List<Artwork> artworks = artworkService.getArtworkList(page, pageSize);
        return Result.success(artworks);
    }

    /**
     * 搜索作品（带筛选条件）
     */
    @GetMapping("/search")
    public Result<List<Artwork>> searchArtworks(@RequestParam(required = false) Integer categoryId,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Double minPrice,
                                                @RequestParam(required = false) Double maxPrice,
                                                @RequestParam(defaultValue = "upload_time") String orderBy,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "12") int pageSize) {
        List<Artwork> artworks = artworkService.searchArtworks(categoryId, keyword, minPrice, maxPrice, orderBy, page, pageSize);
        return Result.success(artworks);
    }
    /**
     * 获取作品详情（增强版 - 包含收藏状态和评论统计）
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getArtworkDetail(@PathVariable Integer id) {
        Artwork artwork = artworkService.getArtworkDetail(id);
        if (artwork == null) {
            return Result.error("作品不存在");
        }

        // 记录浏览行为
        userBehaviorService.recordArtworkView(id, null);

        // 获取收藏信息（需要登录才能获取）
        boolean isFavorited = false;
        try {
            isFavorited = favoriteService.isFavorited(id);
        } catch (Exception e) {
            // 未登录用户，收藏状态为false
        }

        int favoriteCount = favoriteService.getArtworkFavoriteCount(id);

        // 获取评论统计
        Map<String, Object> commentStats = commentService.getCommentStats(id);

        return Result.success(Map.of(
                "artwork", artwork,
                "isFavorited", isFavorited,
                "favoriteCount", favoriteCount,
                "commentStats", commentStats
        ));
    }

    /**
     * 获取我的作品
     */
    @GetMapping("/my")
    public Result<List<Artwork>> getMyArtworks() {
        List<Artwork> artworks = artworkService.getMyArtworks();
        return Result.success(artworks);
    }
}