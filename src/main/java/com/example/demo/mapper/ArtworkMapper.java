package com.example.demo.mapper;

import com.example.demo.pojo.Artwork;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ArtworkMapper {

    /**
     * 发布作品
     */
    void insert(Artwork artwork);

    /**
     * 分页查询作品列表（带关联查询）
     */
    List<Artwork> selectPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 条件查询作品列表
     */
    List<Artwork> selectByCondition(@Param("categoryId") Integer categoryId,
                                    @Param("keyword") String keyword,
                                    @Param("minPrice") Double minPrice,
                                    @Param("maxPrice") Double maxPrice,
                                    @Param("orderBy") String orderBy,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);

    /**
     * 获取作品详情
     */
    Artwork selectById(Integer id);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(Integer id);

    /**
     * 获取用户的作品列表
     */
    List<Artwork> selectByUserId(Integer userId);

    // 更新价格
    void updatePrice(@Param("id") Integer id, @Param("price") BigDecimal price);

    // 新增搜索相关方法
    List<Artwork> searchArtworks(Map<String, Object> params);
    int countSearchResults(Map<String, Object> params);

    // 新增推荐相关方法
    List<Artwork> selectLatestArtworks(int limit);
    List<Artwork> selectByIds(@Param("ids") List<Integer> ids);
    List<Artwork> selectSimilarArtworks(Map<String, Object> params);
    List<Artwork> selectByCategoryOrderByPopular(@Param("categoryId") Integer categoryId, @Param("limit") int limit);

    int countNewArtworksByDate(@Param("date") LocalDate date);
    // 统计用户月度上传作品数
    int countUserArtworksByMonth(@Param("userId") Integer userId, @Param("statMonth") String statMonth);

    // 增加浏览量
    void incrementViewCount(@Param("id") Integer id, @Param("count") Integer count);

    // 标记作品为预售状态
    int markAsPreSold(@Param("artworkId") Integer artworkId);

    // 确认作品已售出
    int markAsSold(@Param("artworkId") Integer artworkId);

    int rollbackToAvailable(@Param("artworkId") Integer artworkId);

    int lockArtworkForPurchase(Integer artworkId);
    int rollbackArtworkStatus(@Param("artworkId") Integer artworkId);
}