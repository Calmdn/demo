package com.example.demo.mapper;

import com.example.demo.pojo.ArtworkPriceHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArtworkPriceHistoryMapper {

    // 插入价格历史记录
    void insert(ArtworkPriceHistory priceHistory);

    // 查询作品价格历史
    List<ArtworkPriceHistory> selectByArtworkId(@Param("artworkId") Integer artworkId,
                                                @Param("offset") int offset,
                                                @Param("pageSize") int pageSize);

    // 查询用户的价格变更记录
    List<ArtworkPriceHistory> selectByOperatorId(@Param("operatorId") Integer operatorId,
                                                 @Param("offset") int offset,
                                                 @Param("pageSize") int pageSize);
}