package com.example.demo.mapper;

import com.example.demo.pojo.ArtworkDiscount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArtworkDiscountMapper {

    // 插入折扣活动
    void insert(ArtworkDiscount discount);

    // 查询作品的当前有效折扣
    ArtworkDiscount selectActiveDiscountByArtworkId(Integer artworkId);

    // 查询作品的所有折扣历史
    List<ArtworkDiscount> selectByArtworkId(@Param("artworkId") Integer artworkId,
                                            @Param("offset") int offset,
                                            @Param("pageSize") int pageSize);

    // 查询用户创建的折扣活动
    List<ArtworkDiscount> selectByCreatorId(@Param("creatorId") Integer creatorId,
                                            @Param("status") Integer status,
                                            @Param("offset") int offset,
                                            @Param("pageSize") int pageSize);

    // 更新折扣状态
    void updateStatus(@Param("id") Integer id,
                      @Param("status") Integer status,
                      @Param("creatorId") Integer creatorId);

    // 批量查询多个作品的有效折扣
    List<ArtworkDiscount> selectActiveDiscountsByArtworkIds(@Param("artworkIds") List<Integer> artworkIds);
}