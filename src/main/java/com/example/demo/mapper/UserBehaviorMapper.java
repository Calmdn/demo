package com.example.demo.mapper;

import com.example.demo.pojo.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorMapper {

    // 插入用户行为记录
    void insert(UserBehavior userBehavior);

    // 获取用户浏览历史
    List<Map<String, Object>> getUserViewHistory(@Param("userId") Integer userId,
                                                 @Param("offset") int offset,
                                                 @Param("pageSize") int pageSize);

    // 获取热门作品
    List<Map<String, Object>> getPopularArtworks(@Param("days") int days, @Param("limit") int limit);

    // 获取用户兴趣偏好
    List<Map<String, Object>> getUserCategoryPreference(@Param("userId") Integer userId, @Param("limit") int limit);

    int countActiveUsersByDate(@Param("date") LocalDate date);
    int countViewsByDate(@Param("date") LocalDate date);
    int countFavoritesByDate(@Param("date") LocalDate date);
    int countCommentsByDate(@Param("date") LocalDate date);
    // 统计最近N分钟内活跃的用户数
    int countRecentActiveUsers(@Param("minutes") int minutes);
    // 获取指定月份的活跃用户
    List<Integer> getActiveUsersInMonth(@Param("statMonth") String statMonth);
    // 统计用户月度行为数据
    int countUserViewsByMonth(@Param("userId") Integer userId, @Param("statMonth") String statMonth);
    int countUserFavoritesByMonth(@Param("userId") Integer userId, @Param("statMonth") String statMonth);
    int countUserCommentsByMonth(@Param("userId") Integer userId, @Param("statMonth") String statMonth);
    int countUserActiveDaysInMonth(@Param("userId") Integer userId, @Param("statMonth") String statMonth);
}