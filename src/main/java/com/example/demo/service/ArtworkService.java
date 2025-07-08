package com.example.demo.service;

import com.example.demo.context.UserContext;
import com.example.demo.mapper.ArtworkMapper;
import com.example.demo.pojo.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtworkService {

    @Autowired
    private ArtworkMapper artworkMapper;

    @Transactional
    @CacheEvict(value = {"artworkList", "userArtworks"}, allEntries = true)  // 发布新作品时清除列表缓存
    public void publishArtwork(Artwork artwork) {
        // 设置当前用户ID
        Integer userId = UserContext.getCurrentUserId();
        artwork.setUserId(userId);

        // 设置默认状态为上架
        artwork.setStatus(1);

        artworkMapper.insert(artwork);
    }

    /**
     * 分页查询作品（添加缓存）
     */
    @Cacheable(value = "artworkList", key = "'page_' + #page + '_size_' + #pageSize")
    public List<Artwork> getArtworkList(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return artworkMapper.selectPage(offset, pageSize);
    }

    /**
     * 条件查询作品（添加缓存）
     */
    @Cacheable(value = "artworkSearch", key = "'search_' + #categoryId + '_' + #keyword + '_' + #minPrice + '_' + #maxPrice + '_' + #orderBy + '_' + #page + '_' + #pageSize")
    public List<Artwork> searchArtworks(Integer categoryId, String keyword, Double minPrice,
                                        Double maxPrice, String orderBy, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return artworkMapper.selectByCondition(categoryId, keyword, minPrice, maxPrice, orderBy, offset, pageSize);
    }

    /**
     * 获取作品详情（添加缓存）
     */
    @Cacheable(value = "artwork", key = "#id")
    public Artwork getArtworkDetail(Integer id) {
        // 增加浏览次数
        artworkMapper.incrementViewCount(id);

        // 返回详情
        return artworkMapper.selectById(id);
    }

    /**
     * 获取我的作品列表（添加缓存）
     */
    @Cacheable(value = "userArtworks", key = "'user_' + T(com.example.demo.context.UserContext).getCurrentUserId()")
    public List<Artwork> getMyArtworks() {
        Integer userId = UserContext.getCurrentUserId();
        return artworkMapper.selectByUserId(userId);
    }
}