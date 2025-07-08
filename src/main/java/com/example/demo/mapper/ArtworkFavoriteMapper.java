package com.example.demo.mapper;

import com.example.demo.pojo.ArtworkFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArtworkFavoriteMapper {

    /**
     * 添加收藏
     */
    void insert(ArtworkFavorite favorite);

    /**
     * 取消收藏
     */
    void delete(@Param("userId") Integer userId, @Param("artworkId") Integer artworkId);

    /**
     * 检查是否已收藏
     */
    int countByUserAndArtwork(@Param("userId") Integer userId, @Param("artworkId") Integer artworkId);

    /**
     * 获取用户收藏列表（带作品信息）
     */
    List<ArtworkFavorite> selectByUserId(Integer userId);

    /**
     * 批量删除收藏
     */
    void batchDelete(@Param("userId") Integer userId, @Param("artworkIds") List<Integer> artworkIds);

    /**
     * 获取用户收藏数量
     */
    int countByUserId(Integer userId);

    /**
     * 获取作品的收藏数量
     */
    int countByArtworkId(Integer artworkId);
}