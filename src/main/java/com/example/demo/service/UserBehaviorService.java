package com.example.demo.service;

import com.example.demo.mapper.UserBehaviorMapper;
import com.example.demo.pojo.UserBehavior;
import com.example.demo.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserBehaviorService {

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private StatisticsDataService statisticsDataService;

    /**
     * 记录用户行为
     */
    public void recordBehavior(UserBehavior behavior) {
        Integer userId = getCurrentUserIdSafely();
        if (userId != null) {
            behavior.setUserId(userId);
            userBehaviorMapper.insert(behavior);
        }
    }

    /**
     * 记录作品浏览行为（增强版）
     */
    public void recordArtworkView(Integer artworkId, Integer duration) {
        UserBehavior behavior = new UserBehavior();
        behavior.setTargetType(1);  // 1-作品
        behavior.setTargetId(artworkId);
        behavior.setBehaviorType(1);  // 1-浏览
        behavior.setDuration(duration);

        recordBehavior(behavior);

        // 同时记录到统计系统
        statisticsDataService.recordArtworkView(artworkId);
    }

    /**
     * 记录收藏行为（增强版）
     */
    public void recordFavoriteBehavior(Integer artworkId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setTargetType(1);  // 1-作品
        behavior.setTargetId(artworkId);
        behavior.setBehaviorType(2);  // 2-收藏

        recordBehavior(behavior);

        // 同时记录到统计系统
        statisticsDataService.recordArtworkFavorite(artworkId);
    }

    /**
     * 记录评论行为（增强版）
     */
    public void recordCommentBehavior(Integer artworkId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setTargetType(1);  // 1-作品
        behavior.setTargetId(artworkId);
        behavior.setBehaviorType(3);  // 3-评论

        recordBehavior(behavior);

        // 同时记录到统计系统
        statisticsDataService.recordArtworkComment(artworkId);
    }

    /**
     * 记录购买行为
     */
    public void recordPurchaseBehavior(Integer artworkId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setTargetType(1);  // 1-作品
        behavior.setTargetId(artworkId);
        behavior.setBehaviorType(4);  // 4-购买

        recordBehavior(behavior);
    }

    /**
     * 获取用户浏览历史
     */
    public List<Map<String, Object>> getUserViewHistory(int page, int pageSize) {
        Integer userId = getCurrentUserIdSafely();
        if (userId == null) {
            return List.of();
        }

        int offset = (page - 1) * pageSize;
        return userBehaviorMapper.getUserViewHistory(userId, offset, pageSize);
    }

    /**
     * 获取用户兴趣偏好
     */
    public List<Map<String, Object>> getUserCategoryPreference(int limit) {
        Integer userId = getCurrentUserIdSafely();
        if (userId == null) {
            return List.of();
        }

        return userBehaviorMapper.getUserCategoryPreference(userId, limit);
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