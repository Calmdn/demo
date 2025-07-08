package com.example.demo.controller;

import com.example.demo.pojo.ArtworkFavorite;
import com.example.demo.common.Result;
import com.example.demo.service.ArtworkFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class ArtworkFavoriteController {

    @Autowired
    private ArtworkFavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/add/{artworkId}")
    public Result<String> addFavorite(@PathVariable Integer artworkId) {
        boolean success = favoriteService.addFavorite(artworkId);
        if (success) {
            return Result.success("收藏成功");
        } else {
            return Result.error("该作品已在收藏夹中");
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/remove/{artworkId}")
    public Result<String> removeFavorite(@PathVariable Integer artworkId) {
        boolean success = favoriteService.removeFavorite(artworkId);
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("该作品不在收藏夹中");
        }
    }

    /**
     * 切换收藏状态
     */
    @PostMapping("/toggle/{artworkId}")
    public Result<Map<String, Object>> toggleFavorite(@PathVariable Integer artworkId) {
        boolean isFavorited = favoriteService.toggleFavorite(artworkId);

        return Result.success(Map.of(
                "isFavorited", isFavorited,
                "message", isFavorited ? "收藏成功" : "取消收藏成功"
        ));
    }

    /**
     * 检查收藏状态
     */
    @GetMapping("/check/{artworkId}")
    public Result<Map<String, Object>> checkFavoriteStatus(@PathVariable Integer artworkId) {
        boolean isFavorited = favoriteService.isFavorited(artworkId);
        int favoriteCount = favoriteService.getArtworkFavoriteCount(artworkId);

        return Result.success(Map.of(
                "isFavorited", isFavorited,
                "favoriteCount", favoriteCount
        ));
    }

    /**
     * 获取我的收藏列表
     */
    @GetMapping("/my")
    public Result<List<ArtworkFavorite>> getMyFavorites() {
        List<ArtworkFavorite> favorites = favoriteService.getMyFavorites();
        return Result.success(favorites);
    }

    /**
     * 批量删除收藏
     */
    @DeleteMapping("/batch")
    public Result<String> batchRemoveFavorites(@RequestBody List<Integer> artworkIds) {
        favoriteService.batchRemoveFavorites(artworkIds);
        return Result.success("批量删除成功");
    }

    /**
     * 获取收藏数量统计
     */
    @GetMapping("/count")
    public Result<Integer> getMyFavoriteCount() {
        int count = favoriteService.getMyFavoriteCount();
        return Result.success(count);
    }
}