package com.example.demo.service;

import com.example.demo.mapper.ArtworkFavoriteMapper;
import com.example.demo.pojo.ArtworkFavorite;
import com.example.demo.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtworkFavoriteService {

    @Autowired
    private ArtworkFavoriteMapper favoriteMapper;
    @Autowired
    private UserBehaviorService userBehaviorService;

    // 注入自己，获取代理对象
//    @Autowired
//    private ArtworkFavoriteService self;
    /**
     * 添加收藏
     */
    @Transactional
    public boolean addFavorite(Integer artworkId) {
        Integer userId = UserContext.getCurrentUserId();

        // 检查是否已收藏
        if (isFavorited(artworkId)) {
            return false; // 已收藏，不能重复添加
        }

        ArtworkFavorite favorite = new ArtworkFavorite();
        favorite.setUserId(userId);
        favorite.setArtworkId(artworkId);

        favoriteMapper.insert(favorite);

        // 记录收藏行为
        userBehaviorService.recordFavoriteBehavior(artworkId);

        return true;
    }

    /**
     * 取消收藏
     */

    @Transactional
    public boolean removeFavorite(Integer artworkId) {
        Integer userId = UserContext.getCurrentUserId();

        // 检查是否已收藏
        if (!isFavorited(artworkId)) {
            return false; // 未收藏，无法取消
        }

        favoriteMapper.delete(userId, artworkId);
        return true;
    }

    /**
     * 切换收藏状态（收藏/取消收藏）
     */
    @Transactional
    public boolean toggleFavorite(Integer artworkId) {
        if (isFavorited(artworkId)) {
            return !removeFavorite(artworkId); // 取消收藏返回false
        } else {
            boolean added = addFavorite(artworkId);
            return added; // 添加收藏返回true
        }
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorited(Integer artworkId) {
        Integer userId = UserContext.getCurrentUserId();
        return favoriteMapper.countByUserAndArtwork(userId, artworkId) > 0;
    }

    /**
     * 获取我的收藏列表
     */
    public List<ArtworkFavorite> getMyFavorites() {
        Integer userId = UserContext.getCurrentUserId();
        return favoriteMapper.selectByUserId(userId);
    }

    /**
     * 批量删除收藏
     */
    public void batchRemoveFavorites(List<Integer> artworkIds) {
        if (artworkIds == null || artworkIds.isEmpty()) {
            return;
        }

        Integer userId = UserContext.getCurrentUserId();
        favoriteMapper.batchDelete(userId, artworkIds);
    }

    /**
     * 获取我的收藏数量
     */
    public int getMyFavoriteCount() {
        Integer userId = UserContext.getCurrentUserId();
        return favoriteMapper.countByUserId(userId);
    }

    /**
     * 获取作品的收藏数量
     */
    public int getArtworkFavoriteCount(Integer artworkId) {
        return favoriteMapper.countByArtworkId(artworkId);
    }
}