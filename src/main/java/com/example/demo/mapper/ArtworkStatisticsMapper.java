package com.example.demo.mapper;

import com.example.demo.pojo.ArtworkStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ArtworkStatisticsMapper {

    // 插入作品统计
    void insert(ArtworkStatistics artworkStatistics);

    // 获取热门作品排行
    List<Map<String, Object>> getPopularArtworksRanking(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate,
                                                        @Param("sortBy") String sortBy,
                                                        @Param("limit") int limit);

    // 获取作品详细统计
    List<ArtworkStatistics> getArtworkDetailStatistics(@Param("artworkId") Integer artworkId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    // 获取分类作品统计
    List<Map<String, Object>> getCategoryStatistics(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
}