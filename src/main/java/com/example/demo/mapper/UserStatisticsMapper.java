package com.example.demo.mapper;

import com.example.demo.pojo.UserStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserStatisticsMapper {

    // 插入用户统计
    void insert(UserStatistics userStatistics);

    // 获取用户活跃度排行
    List<Map<String, Object>> getUserActivityRanking(@Param("startMonth") String startMonth,
                                                     @Param("endMonth") String endMonth,
                                                     @Param("sortBy") String sortBy,
                                                     @Param("limit") int limit);

    // 获取用户详细统计
    List<UserStatistics> getUserDetailStatistics(@Param("userId") Integer userId,
                                                 @Param("startMonth") String startMonth,
                                                 @Param("endMonth") String endMonth);

    // 获取新用户增长趋势
    List<Map<String, Object>> getNewUserTrends(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}