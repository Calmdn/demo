package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.pojo.StatisticsRequest;
import com.example.demo.pojo.StatisticsResponse;
import com.example.demo.service.OrderService;
import com.example.demo.service.StatisticsService;
import com.example.demo.service.StatisticsDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private StatisticsDataService statisticsDataService;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取平台总览统计
     */
    @PostMapping("/platform/overview")
    public Result<StatisticsResponse> getPlatformOverview(@RequestBody StatisticsRequest request) {
        try {
            StatisticsResponse response = statisticsService.getPlatformOverview(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("获取平台统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取平台总览统计（GET方式，使用默认参数）
     */
    @GetMapping("/platform/overview")
    public Result<StatisticsResponse> getPlatformOverviewDefault(@RequestParam(defaultValue = "week") String timeRange) {
        StatisticsRequest request = new StatisticsRequest();
        request.setTimeRange(timeRange);
        return getPlatformOverview(request);
    }

    /**
     * 获取用户统计分析
     */
    @PostMapping("/users/analytics")
    public Result<StatisticsResponse> getUserAnalytics(@RequestBody StatisticsRequest request) {
        try {
            StatisticsResponse response = statisticsService.getUserAnalytics(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("获取用户统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户活跃度排行
     */
    @GetMapping("/users/ranking")
    public Result<StatisticsResponse> getUserRanking(@RequestParam(defaultValue = "month") String timeRange,
                                                     @RequestParam(defaultValue = "totalArtworks") String sortBy) {
        StatisticsRequest request = new StatisticsRequest();
        request.setTimeRange(timeRange);
        request.setSortBy(sortBy);
        return getUserAnalytics(request);
    }

    /**
     * 获取作品统计分析
     */
    @PostMapping("/artworks/analytics")
    public Result<StatisticsResponse> getArtworkAnalytics(@RequestBody StatisticsRequest request) {
        try {
            StatisticsResponse response = statisticsService.getArtworkAnalytics(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("获取作品统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取热门作品排行
     */
    @GetMapping("/artworks/popular")
    public Result<StatisticsResponse> getPopularArtworks(@RequestParam(defaultValue = "week") String timeRange,
                                                         @RequestParam(defaultValue = "totalViews") String sortBy) {
        StatisticsRequest request = new StatisticsRequest();
        request.setTimeRange(timeRange);
        request.setSortBy(sortBy);
        return getArtworkAnalytics(request);
    }

    /**
     * 获取个人统计报告
     */
    @GetMapping("/personal/report")
    public Result<StatisticsResponse> getPersonalReport() {
        try {
            StatisticsResponse response = statisticsService.getPersonalReport();
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("获取个人统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取实时统计数据
     */
    @GetMapping("/realtime")
    public Result<Map<String, Object>> getRealTimeStats() {
        try {
            Map<String, Object> stats = statisticsService.getRealTimeStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取实时统计失败：" + e.getMessage());
        }
    }

    /**
     * 手动触发统计数据收集（管理员功能）
     */
    @PostMapping("/collect")
    public Result<String> manualCollectStatistics(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            statisticsDataService.manualCollectStatistics(date);
            return Result.success("统计数据收集成功");
        } catch (Exception e) {
            return Result.error("统计数据收集失败：" + e.getMessage());
        }
    }

    /**
     * 获取分类统计对比
     */
    @GetMapping("/categories/comparison")
    public Result<StatisticsResponse> getCategoryComparison(@RequestParam(defaultValue = "month") String timeRange) {
        StatisticsRequest request = new StatisticsRequest();
        request.setTimeRange(timeRange);
        request.setDimension("category");
        return getArtworkAnalytics(request);
    }
    /**
     * 获取收入统计分析
     */
    @PostMapping("/revenue/analytics")
    public Result<StatisticsResponse> getRevenueAnalytics(@RequestBody StatisticsRequest request) {
        try {
            StatisticsResponse response = statisticsService.getRevenueAnalytics(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("获取收入统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取收入趋势
     */
    @GetMapping("/revenue/trends")
    public Result<StatisticsResponse> getRevenueTrends(@RequestParam(defaultValue = "month") String timeRange) {
        StatisticsRequest request = new StatisticsRequest();
        request.setTimeRange(timeRange);
        return getRevenueAnalytics(request);
    }

    /**
     * 获取热销作品排行
     */
    @GetMapping("/revenue/top-selling")
    public Result<List<Map<String, Object>>> getTopSellingArtworks(@RequestParam(defaultValue = "month") String timeRange,
                                                                   @RequestParam(defaultValue = "10") int limit) {
        StatisticsRequest request = new StatisticsRequest();
        request.setTimeRange(timeRange);

        LocalDate[] dateRange = parseDateRange(request);
        List<Map<String, Object>> topSelling = orderMapper.getTopSellingArtworks(
                dateRange[0], dateRange[1], limit);

        return Result.success(topSelling);
    }

    // 辅助方法
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
}