package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.pojo.UserBehavior;
import com.example.demo.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/behavior")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    /**
     * 记录用户行为
     */
    @PostMapping("/record")
    public Result<String> recordBehavior(@RequestBody UserBehavior behavior) {
        try {
            userBehaviorService.recordBehavior(behavior);
            return Result.success("记录成功");
        } catch (Exception e) {
            return Result.error("记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户浏览历史
     */
    @GetMapping("/history/view")
    public Result<List<Map<String, Object>>> getUserViewHistory(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "20") int pageSize) {
        List<Map<String, Object>> history = userBehaviorService.getUserViewHistory(page, pageSize);
        return Result.success(history);
    }

    /**
     * 获取用户兴趣偏好
     */
    @GetMapping("/preference")
    public Result<List<Map<String, Object>>> getUserPreference(@RequestParam(defaultValue = "5") int limit) {
        List<Map<String, Object>> preferences = userBehaviorService.getUserCategoryPreference(limit);
        return Result.success(preferences);
    }
}