package com.example.demo.service;

import com.example.demo.mapper.ArtworkMapper;
import com.example.demo.mapper.UserBehaviorMapper;
import com.example.demo.pojo.Artwork;
import com.example.demo.pojo.RecommendRequest;
import com.example.demo.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    /**
     * 获取推荐作品
     */
    public List<Artwork> getRecommendedArtworks(RecommendRequest request) {
        String recommendType = request.getRecommendType();

        switch (recommendType) {
            case "hot":
                return getHotArtworks(request.getCount());
            case "similar":
                return getSimilarArtworks(request.getArtworkId(), request.getCount());
            case "personalized":
                return getPersonalizedRecommendations(request.getUserId(), request.getCount());
            default:
                return getDefaultRecommendations(request.getCount());
        }
    }

    /**
     * 获取热门作品推荐
     */
    public List<Artwork> getHotArtworks(int count) {
        // 基于最近7天的用户行为数据
        List<Map<String, Object>> popularData = userBehaviorMapper.getPopularArtworks(7, count * 2);

        List<Integer> artworkIds = popularData.stream()
                .map(data -> (Integer) data.get("artworkId"))
                .collect(Collectors.toList());

        if (artworkIds.isEmpty()) {
            // 降级：返回最新发布的作品
            return artworkMapper.selectLatestArtworks(count);
        }

        return artworkMapper.selectByIds(artworkIds);
    }

    /**
     * 获取相似作品推荐
     */
    public List<Artwork> getSimilarArtworks(Integer artworkId, int count) {
        if (artworkId == null) {
            return getHotArtworks(count);
        }

        Artwork artwork = artworkMapper.selectById(artworkId);
        if (artwork == null) {
            return getHotArtworks(count);
        }

        // 基于分类找相似作品
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", artwork.getCategoryId());
        params.put("excludeId", artworkId);
        params.put("limit", count);

        List<Artwork> similarArtworks = artworkMapper.selectSimilarArtworks(params);

        // 如果相似作品不够，补充热门作品
        if (similarArtworks.size() < count) {
            List<Artwork> hotArtworks = getHotArtworks(count - similarArtworks.size());
            similarArtworks.addAll(hotArtworks);
        }

        return similarArtworks.stream()
                .distinct()
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * 获取个性化推荐
     */
    public List<Artwork> getPersonalizedRecommendations(Integer userId, int count) {
        if (userId == null) {
            userId = getCurrentUserIdSafely();
        }

        if (userId == null) {
            return getHotArtworks(count);
        }

        // 获取用户兴趣偏好
        List<Map<String, Object>> preferences = userBehaviorMapper.getUserCategoryPreference(userId, 5);

        if (preferences.isEmpty()) {
            return getHotArtworks(count);
        }

        List<Artwork> recommendations = new ArrayList<>();

        // 基于用户偏好的分类推荐作品
        for (Map<String, Object> preference : preferences) {
            Integer categoryId = (Integer) preference.get("category_id");
            if (categoryId != null) {
                List<Artwork> categoryArtworks = artworkMapper.selectByCategoryOrderByPopular(categoryId, count / preferences.size() + 1);
                recommendations.addAll(categoryArtworks);
            }
        }

        // 去重并限制数量
        recommendations = recommendations.stream()
                .distinct()
                .limit(count)
                .collect(Collectors.toList());

        // 如果推荐不够，补充热门作品
        if (recommendations.size() < count) {
            List<Artwork> hotArtworks = getHotArtworks(count - recommendations.size());
            recommendations.addAll(hotArtworks);
        }

        return recommendations;
    }

    /**
     * 获取默认推荐（混合推荐）
     */
    public List<Artwork> getDefaultRecommendations(int count) {
        // 简化实现：直接返回最新作品
        return artworkMapper.selectLatestArtworks(count);
    }

    /**
     * 安全获取当前用户ID
     */
    private Integer getCurrentUserIdSafely() {
        try {
            return UserContext.getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}