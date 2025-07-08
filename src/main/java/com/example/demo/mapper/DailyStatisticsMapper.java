package com.example.demo.mapper;

import com.example.demo.pojo.DailyStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface DailyStatisticsMapper {

    // 插入每日统计
    void insert(DailyStatistics dailyStatistics);

    // 获取日期范围内的统计数据
    List<DailyStatistics> selectByDateRange(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // 获取平台概览数据
    Map<String, Object> getPlatformOverview(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // 获取增长趋势数据
    List<Map<String, Object>> getGrowthTrends(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // 获取最新统计数据
    DailyStatistics getLatestStatistics();
}