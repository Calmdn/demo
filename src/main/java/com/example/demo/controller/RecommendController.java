package com.example.demo.controller;

import com.example.demo.pojo.Artwork;
import com.example.demo.pojo.RecommendRequest;
import com.example.demo.common.Result;
import com.example.demo.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取推荐作品
     */
    @PostMapping
    public Result<List<Artwork>> getRecommendedArtworks(@RequestBody RecommendRequest request) {
        try {
            List<Artwork> artworks = recommendService.getRecommendedArtworks(request);
            return Result.success(artworks);
        } catch (Exception e) {
            return Result.error("获取推荐失败：" + e.getMessage());
        }
    }

    /**
     * 获取热门作品
     */
    @GetMapping("/hot")
    public Result<List<Artwork>> getHotArtworks(@RequestParam(defaultValue = "10") int count) {
        List<Artwork> artworks = recommendService.getHotArtworks(count);
        return Result.success(artworks);
    }

    /**
     * 获取相似作品
     */
    @GetMapping("/similar/{artworkId}")
    public Result<List<Artwork>> getSimilarArtworks(@PathVariable Integer artworkId,
                                                    @RequestParam(defaultValue = "10") int count) {
        List<Artwork> artworks = recommendService.getSimilarArtworks(artworkId, count);
        return Result.success(artworks);
    }

    /**
     * 获取个性化推荐
     */
    @GetMapping("/personalized")
    public Result<List<Artwork>> getPersonalizedRecommendations(@RequestParam(defaultValue = "10") int count) {
        RecommendRequest request = new RecommendRequest();
        request.setRecommendType("personalized");
        request.setCount(count);

        List<Artwork> artworks = recommendService.getRecommendedArtworks(request);
        return Result.success(artworks);
    }
}