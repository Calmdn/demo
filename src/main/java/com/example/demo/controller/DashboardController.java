package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.pojo.StatisticsRequest;
import com.example.demo.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取管理员仪表板数据
     */
    @GetMapping("/admin")
    public Result<Map<String, Object>> getAdminDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();

            // 实时数据
            dashboard.put("realtime", statisticsService.getRealTimeStats());

            // 本周概览
            StatisticsRequest weekRequest = new StatisticsRequest();
            weekRequest.setTimeRange("week");
            dashboard.put("weekOverview", statisticsService.getPlatformOverview(weekRequest));

            // 本月趋势
            StatisticsRequest monthRequest = new StatisticsRequest();
            monthRequest.setTimeRange("month");
            dashboard.put("monthTrends", statisticsService.getPlatformOverview(monthRequest));

            return Result.success(dashboard);
        } catch (Exception e) {
            return Result.error("获取仪表板数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户个人仪表板数据
     */
    @GetMapping("/personal")
    public Result<Map<String, Object>> getPersonalDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();

            // 个人统计报告
            dashboard.put("personalReport", statisticsService.getPersonalReport());

            // 个人作品表现（简化版）
            StatisticsRequest artworkRequest = new StatisticsRequest();
            artworkRequest.setTimeRange("month");
            dashboard.put("artworkPerformance", statisticsService.getArtworkAnalytics(artworkRequest));

            return Result.success(dashboard);
        } catch (Exception e) {
            return Result.error("获取个人仪表板失败：" + e.getMessage());
        }
    }
}