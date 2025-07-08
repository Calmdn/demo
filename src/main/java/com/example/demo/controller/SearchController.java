package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.pojo.SearchLog;
import com.example.demo.pojo.SearchRequest;
import com.example.demo.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 搜索作品
     */
    @PostMapping("/artworks")
    public Result<Map<String, Object>> searchArtworks(@RequestBody SearchRequest request) {
        try {
            Map<String, Object> result = searchService.searchArtworks(request);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("搜索失败：" + e.getMessage());
        }
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public Result<List<String>> getSearchSuggestions(@RequestParam String keyword) {
        List<String> suggestions = searchService.getSearchSuggestions(keyword);
        return Result.success(suggestions);
    }

    /**
     * 获取热门搜索词
     */
    @GetMapping("/hot-keywords")
    public Result<List<Map<String, Object>>> getHotKeywords(@RequestParam(defaultValue = "7") int days,
                                                            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> hotKeywords = searchService.getHotKeywords(days, limit);
        return Result.success(hotKeywords);
    }

    /**
     * 获取用户搜索历史
     */
    @GetMapping("/history")
    public Result<List<SearchLog>> getUserSearchHistory(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "20") int pageSize) {
        List<SearchLog> history = searchService.getUserSearchHistory(page, pageSize);
        return Result.success(history);
    }
}