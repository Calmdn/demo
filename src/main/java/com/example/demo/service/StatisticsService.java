package com.example.demo.service;

import com.example.demo.mapper.*;
import com.example.demo.pojo.*;
import com.example.demo.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class StatisticsService {

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
     * 获取平台总览统计
     */
    public StatisticsResponse getPlatformOverview(StatisticsRequest request) {
        LocalDate[] dateRange = parseDateRange(request);
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        StatisticsResponse response = new StatisticsResponse();

        // 概览数据
        Map<String, Object> overview = dailyStatisticsMapper.getPlatformOverview(startDate, endDate);
        response.setOverview(overview);

        // 趋势数据
        List<Map<String, Object>> trends = dailyStatisticsMapper.getGrowthTrends(startDate, endDate);
        response.setTrends(trends);

        // 分类统计
        List<Map<String, Object>> categoryStats = artworkStatisticsMapper.getCategoryStatistics(startDate, endDate);
        response.setComparisons(categoryStats);

        // 元数据
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("startDate", startDate);
        metadata.put("endDate", endDate);
        metadata.put("timeRange", request.getTimeRange());
        response.setMetadata(metadata);

        return response;
    }

    /**
     * 获取用户统计分析
     */
    public StatisticsResponse getUserAnalytics(StatisticsRequest request) {
        String[] monthRange = parseMonthRange(request);
        String startMonth = monthRange[0];
        String endMonth = monthRange[1];

        StatisticsResponse response = new StatisticsResponse();

        // 用户活跃度排行
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "totalArtworks";
        List<Map<String, Object>> rankings = userStatisticsMapper.getUserActivityRanking(
                startMonth, endMonth, sortBy, 20);
        response.setRankings(rankings);

        // 新用户增长趋势
        LocalDate[] dateRange = parseDateRange(request);
        List<Map<String, Object>> trends = userStatisticsMapper.getNewUserTrends(
                dateRange[0], dateRange[1]);
        response.setTrends(trends);

        // 概览数据
        Map<String, Object> overview = calculateUserOverview(rankings);
        response.setOverview(overview);

        return response;
    }

    /**
     * 获取作品统计分析
     */
    public StatisticsResponse getArtworkAnalytics(StatisticsRequest request) {
        LocalDate[] dateRange = parseDateRange(request);
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        StatisticsResponse response = new StatisticsResponse();

        // 热门作品排行
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "totalViews";
        List<Map<String, Object>> rankings = artworkStatisticsMapper.getPopularArtworksRanking(
                startDate, endDate, sortBy, 20);
        response.setRankings(rankings);

        // 分类统计对比
        List<Map<String, Object>> comparisons = artworkStatisticsMapper.getCategoryStatistics(
                startDate, endDate);
        response.setComparisons(comparisons);

        // 概览数据
        Map<String, Object> overview = calculateArtworkOverview(rankings, comparisons);
        response.setOverview(overview);

        return response;
    }

    /**
     * 获取个人统计报告
     */
    public StatisticsResponse getPersonalReport() {
        Integer userId = UserContext.getCurrentUserId();
        StatisticsRequest request = new StatisticsRequest();
        request.setTimeRange("month");
        request.setUserId(userId);

        String[] monthRange = parseMonthRange(request);
        LocalDate[] dateRange = parseDateRange(request);

        StatisticsResponse response = new StatisticsResponse();

        // 个人详细统计
        List<UserStatistics> userStats = userStatisticsMapper.getUserDetailStatistics(
                userId, monthRange[0], monthRange[1]);

        // 个人作品表现
        Map<String, Object> personalOverview = calculatePersonalOverview(userId, dateRange[0], dateRange[1]);
        response.setOverview(personalOverview);

        // 趋势数据（转换用户统计为趋势格式）
        List<Map<String, Object>> trends = convertUserStatsToTrends(userStats);
        response.setTrends(trends);

        return response;
    }


    /**
     * 更新实时统计数据，包含收入信息
     */
    public Map<String, Object> getRealTimeStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDate today = LocalDate.now();

        // 今日数据
        stats.put("todayNewUsers", getTodayNewUsers());
        stats.put("todayActiveUsers", getTodayActiveUsers());
        stats.put("todayNewArtworks", getTodayNewArtworks());
        stats.put("todayViews", getTodayViews());
        stats.put("todaySearches", getTodaySearches());

        // 新增：今日收入和订单数据
        stats.put("todayRevenue", orderMapper.calculateDailyRevenue(today));
        stats.put("todayOrders", orderMapper.countOrdersByDate(today));

        // 在线用户（简化实现）
        stats.put("onlineUsers", getEstimatedOnlineUsers());

        // 热门搜索词
        List<Map<String, Object>> hotKeywords = searchLogMapper.getHotKeywords(1, 5);
        stats.put("hotKeywords", hotKeywords);

        return stats;
    }

    /**
     * 获取收入统计分析
     */
    public StatisticsResponse getRevenueAnalytics(StatisticsRequest request) {
        LocalDate[] dateRange = parseDateRange(request);
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        StatisticsResponse response = new StatisticsResponse();

        // 收入概览
        Map<String, Object> overview = new HashMap<>();
        BigDecimal totalRevenue = orderMapper.calculateRevenueByDateRange(startDate, endDate);
        overview.put("totalRevenue", totalRevenue);
        overview.put("avgDailyRevenue", calculateAvgDailyRevenue(totalRevenue, startDate, endDate));
        response.setOverview(overview);

        // 收入趋势
        List<Map<String, Object>> trends = orderMapper.getRevenueTrends(startDate, endDate);
        response.setTrends(trends);

        // 热销作品排行
        List<Map<String, Object>> rankings = orderMapper.getTopSellingArtworks(startDate, endDate, 20);
        response.setRankings(rankings);

        return response;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 解析日期范围
     */
    private LocalDate[] parseDateRange(StatisticsRequest request) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        if (request.getStartDate() != null && request.getEndDate() != null) {
            return new LocalDate[]{request.getStartDate(), request.getEndDate()};
        }

        String timeRange = request.getTimeRange() != null ? request.getTimeRange() : "week";
        switch (timeRange) {
            case "today":
                startDate = endDate;
                break;
            case "week":
                startDate = endDate.minusDays(7);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "quarter":
                startDate = endDate.minusMonths(3);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            default:
                startDate = endDate.minusDays(7);
        }

        return new LocalDate[]{startDate, endDate};
    }

    /**
     * 解析月份范围
     */
    private String[] parseMonthRange(StatisticsRequest request) {
        LocalDate[] dateRange = parseDateRange(request);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return new String[]{
                dateRange[0].format(formatter),
                dateRange[1].format(formatter)
        };
    }

    /**
     * 计算用户概览数据
     */
    private Map<String, Object> calculateUserOverview(List<Map<String, Object>> rankings) {
        Map<String, Object> overview = new HashMap<>();

        if (rankings.isEmpty()) {
            return overview;
        }

        int totalActiveUsers = rankings.size();
        long totalArtworks = rankings.stream()
                .mapToLong(r -> ((Number) r.getOrDefault("totalArtworks", 0)).longValue())
                .sum();
        long totalViews = rankings.stream()
                .mapToLong(r -> ((Number) r.getOrDefault("totalViewed", 0)).longValue())
                .sum();

        overview.put("totalActiveUsers", totalActiveUsers);
        overview.put("totalArtworks", totalArtworks);
        overview.put("totalViews", totalViews);
        overview.put("avgArtworksPerUser", totalActiveUsers > 0 ? (double) totalArtworks / totalActiveUsers : 0);

        return overview;
    }

    private BigDecimal calculateAvgDailyRevenue(BigDecimal totalRevenue, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days <= 0) return BigDecimal.ZERO;
        return totalRevenue.divide(new BigDecimal(days), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算作品概览数据
     */
    private Map<String, Object> calculateArtworkOverview(List<Map<String, Object>> rankings,
                                                         List<Map<String, Object>> comparisons) {
        Map<String, Object> overview = new HashMap<>();

        long totalViews = rankings.stream()
                .mapToLong(r -> ((Number) r.getOrDefault("totalViews", 0)).longValue())
                .sum();
        long totalFavorites = rankings.stream()
                .mapToLong(r -> ((Number) r.getOrDefault("totalFavorites", 0)).longValue())
                .sum();

        overview.put("totalViews", totalViews);
        overview.put("totalFavorites", totalFavorites);
        overview.put("totalCategories", comparisons.size());
        overview.put("topCategory", comparisons.isEmpty() ? null : comparisons.get(0).get("categoryName"));

        return overview;
    }

    /**
     * 计算个人概览数据
     */
    private Map<String, Object> calculatePersonalOverview(Integer userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> overview = new HashMap<>();

        // 获取用户基本信息（这里需要根据实际数据库查询）
        // 简化实现，你可以根据需要扩展
        overview.put("userId", userId);
        overview.put("period", startDate + " 至 " + endDate);

        return overview;
    }

    /**
     * 转换用户统计为趋势数据
     */
    private List<Map<String, Object>> convertUserStatsToTrends(List<UserStatistics> userStats) {
        List<Map<String, Object>> trends = new ArrayList<>();

        for (UserStatistics stat : userStats) {
            Map<String, Object> trend = new HashMap<>();
            trend.put("month", stat.getStatMonth());
            trend.put("artworksUploaded", stat.getArtworksUploaded());
            trend.put("artworksViewed", stat.getArtworksViewed());
            trend.put("artworksFavorited", stat.getArtworksFavorited());
            trend.put("commentsMade", stat.getCommentsMade());
            trends.add(trend);
        }

        return trends;
    }


    // ==================== 实时数据获取方法 ====================

    private int getTodayNewUsers() {
        LocalDate today = LocalDate.now();
        return userMapper.countNewUsersByDate(today);
    }

    private int getTodayActiveUsers() {
        LocalDate today = LocalDate.now();
        return userBehaviorMapper.countActiveUsersByDate(today);
    }

    private int getTodayNewArtworks() {
        LocalDate today = LocalDate.now();
        return artworkMapper.countNewArtworksByDate(today);
    }

    private int getTodayViews() {
        LocalDate today = LocalDate.now();
        return userBehaviorMapper.countViewsByDate(today);
    }

    private int getTodaySearches() {
        LocalDate today = LocalDate.now();
        return searchLogMapper.countSearchesByDate(today);
    }

    private int getEstimatedOnlineUsers() {
        // 基于最近15分钟有行为的用户估算在线数
        return userBehaviorMapper.countRecentActiveUsers(15);
    }
}