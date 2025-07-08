package com.example.demo.service;

import com.example.demo.mapper.*;
import com.example.demo.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsDataService {

    @Autowired
    private DailyStatisticsMapper dailyStatisticsMapper;

    @Autowired
    private UserStatisticsMapper userStatisticsMapper;

    @Autowired
    private ArtworkStatisticsMapper artworkStatisticsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private SearchLogMapper searchLogMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 每日统计数据收集任务
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void collectDailyStatistics() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        collectDailyStatistics(yesterday);
    }

    /**
     * 收集指定日期的统计数据
     */
    public void collectDailyStatistics(LocalDate date) {
        DailyStatistics stats = new DailyStatistics();
        stats.setStatDate(date);

        // 新增用户数
        stats.setNewUsers(getNewUsersCount(date));

        // 活跃用户数
        stats.setActiveUsers(getActiveUsersCount(date));

        // 新增作品数
        stats.setNewArtworks(getNewArtworksCount(date));

        // 总浏览量
        stats.setTotalViews(getTotalViewsCount(date));

        // 总收藏量
        stats.setTotalFavorites(getTotalFavoritesCount(date));

        // 总评论数
        stats.setTotalComments(getTotalCommentsCount(date));

        // 总搜索次数
        stats.setTotalSearches(getTotalSearchesCount(date));

        // 当日收入
        stats.setRevenue(getDailyRevenue(date));

        dailyStatisticsMapper.insert(stats);
    }

    /**
     * 获取指定日期的收入
     */
    private BigDecimal getDailyRevenue(LocalDate date) {
        return orderMapper.calculateDailyRevenue(date);
    }


    /**
     * 获取今日订单数
     */
    private int getTodayOrderCount() {
        LocalDate today = LocalDate.now();
        return orderMapper.countOrdersByDate(today);
    }

    /**
     * 获取今日收入
     */
    private BigDecimal getTodayRevenue() {
        LocalDate today = LocalDate.now();
        return orderMapper.calculateDailyRevenue(today);
    }

    /**
     * 每月用户统计数据收集
     * 每月1号凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    @Transactional
    public void collectMonthlyUserStatistics() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        String statMonth = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 这里应该查询所有用户，为每个用户生成月度统计
        // 简化实现，实际应该批量处理
        collectUserStatisticsForMonth(statMonth);
    }


    private void collectUserStatisticsForMonth(String statMonth) {
        // 获取所有活跃用户ID
        List<Integer> activeUserIds = userBehaviorMapper.getActiveUsersInMonth(statMonth);

        // 为每个用户收集月度统计
        for (Integer userId : activeUserIds) {
            UserStatistics userStats = new UserStatistics();
            userStats.setUserId(userId);
            userStats.setStatMonth(statMonth);

            // 统计该用户在该月的各项数据
            userStats.setArtworksUploaded(getUserArtworksUploadedInMonth(userId, statMonth));
            userStats.setArtworksViewed(getUserArtworksViewedInMonth(userId, statMonth));
            userStats.setArtworksFavorited(getUserArtworksFavoritedInMonth(userId, statMonth));
            userStats.setCommentsMade(getUserCommentsMadeInMonth(userId, statMonth));
            userStats.setSearchesMade(getUserSearchesMadeInMonth(userId, statMonth));
            userStats.setLoginDays(getUserLoginDaysInMonth(userId, statMonth));
            userStats.setTotalRevenue(getUserRevenueInMonth(userId, statMonth));

            // 插入或更新统计数据
            userStatisticsMapper.insert(userStats);
        }
    }


    /**
     * 手动触发统计数据收集
     */
    public void manualCollectStatistics(LocalDate date) {
        collectDailyStatistics(date);
    }

    /**
     * 记录作品浏览统计
     */
    public void recordArtworkView(Integer artworkId) {
        ArtworkStatistics stats = new ArtworkStatistics();
        stats.setArtworkId(artworkId);
        stats.setStatDate(LocalDate.now());
        stats.setViewCount(1);
        stats.setFavoriteCount(0);
        stats.setCommentCount(0);
        stats.setShareCount(0);

        artworkStatisticsMapper.insert(stats);
    }

    /**
     * 记录作品收藏统计
     */
    public void recordArtworkFavorite(Integer artworkId) {
        ArtworkStatistics stats = new ArtworkStatistics();
        stats.setArtworkId(artworkId);
        stats.setStatDate(LocalDate.now());
        stats.setViewCount(0);
        stats.setFavoriteCount(1);
        stats.setCommentCount(0);
        stats.setShareCount(0);

        artworkStatisticsMapper.insert(stats);
    }

    /**
     * 记录作品评论统计
     */
    public void recordArtworkComment(Integer artworkId) {
        ArtworkStatistics stats = new ArtworkStatistics();
        stats.setArtworkId(artworkId);
        stats.setStatDate(LocalDate.now());
        stats.setViewCount(0);
        stats.setFavoriteCount(0);
        stats.setCommentCount(1);
        stats.setShareCount(0);

        artworkStatisticsMapper.insert(stats);
    }

    // ==================== 私有数据查询方法 ====================

    /**
     * 获取新增用户数
     */
    private int getNewUsersCount(LocalDate date) {
        // 查询指定日期注册的用户数
        return userMapper.countNewUsersByDate(date);
    }

    /**
     * 获取活跃用户数
     */
    private int getActiveUsersCount(LocalDate date) {
        // 查询指定日期有行为的用户数
        return userBehaviorMapper.countActiveUsersByDate(date);
    }

    /**
     * 获取新增作品数
     */
    private int getNewArtworksCount(LocalDate date) {
        // 查询指定日期上传的作品数
        return artworkMapper.countNewArtworksByDate(date);
    }

    /**
     * 获取总浏览量
     */
    private int getTotalViewsCount(LocalDate date) {
        // 查询指定日期的总浏览量
        return userBehaviorMapper.countViewsByDate(date);
    }

    /**
     * 获取总收藏量
     */
    private int getTotalFavoritesCount(LocalDate date) {
        // 查询指定日期的总收藏量
        return userBehaviorMapper.countFavoritesByDate(date);
    }

    /**
     * 获取总评论数
     */
    private int getTotalCommentsCount(LocalDate date) {
        // 查询指定日期的总评论数
        return userBehaviorMapper.countCommentsByDate(date);
    }

    /**
     * 获取总搜索次数
     */
    private int getTotalSearchesCount(LocalDate date) {
        // 查询指定日期的总搜索次数
        return searchLogMapper.countSearchesByDate(date);
    }

    /**
     * 获取用户在指定月份上传的作品数
     */
    private int getUserArtworksUploadedInMonth(Integer userId, String statMonth) {
        return artworkMapper.countUserArtworksByMonth(userId, statMonth);
    }

    /**
     * 获取用户在指定月份浏览的作品数
     */
    private int getUserArtworksViewedInMonth(Integer userId, String statMonth) {
        return userBehaviorMapper.countUserViewsByMonth(userId, statMonth);
    }

    /**
     * 获取用户在指定月份收藏的作品数
     */
    private int getUserArtworksFavoritedInMonth(Integer userId, String statMonth) {
        return userBehaviorMapper.countUserFavoritesByMonth(userId, statMonth);
    }

    /**
     * 获取用户在指定月份发表的评论数
     */
    private int getUserCommentsMadeInMonth(Integer userId, String statMonth) {
        return userBehaviorMapper.countUserCommentsByMonth(userId, statMonth);
    }

    /**
     * 获取用户在指定月份的搜索次数
     */
    private int getUserSearchesMadeInMonth(Integer userId, String statMonth) {
        return searchLogMapper.countUserSearchesByMonth(userId, statMonth);
    }

    /**
     * 获取用户在指定月份的登录天数
     */
    private int getUserLoginDaysInMonth(Integer userId, String statMonth) {
        // 基于行为表估算活跃天数
        return userBehaviorMapper.countUserActiveDaysInMonth(userId, statMonth);
    }

    /**
     * 获取用户在指定月份的收入
     */
    private BigDecimal getUserRevenueInMonth(Integer userId, String statMonth) {
        return orderMapper.calculateUserRevenueByMonth(userId, statMonth);
    }
}