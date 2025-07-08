package com.example.demo.service;

import com.example.demo.mapper.ArtworkMapper;
import com.example.demo.mapper.SearchLogMapper;
import com.example.demo.pojo.Artwork;
import com.example.demo.pojo.SearchLog;
import com.example.demo.pojo.SearchRequest;
import com.example.demo.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private ArtworkMapper artworkMapper;

    @Autowired
    private SearchLogMapper searchLogMapper;

    @Autowired
    private HttpServletRequest request;

    /**
     * 搜索作品
     */
    public Map<String, Object> searchArtworks(SearchRequest searchRequest) {
        // 记录搜索日志
        recordSearchLog(searchRequest.getKeyword(), 1);

        // 构建搜索条件
        Map<String, Object> params = buildSearchParams(searchRequest);

        // 执行搜索
        List<Artwork> artworks = artworkMapper.searchArtworks(params);

        // 获取总数
        int total = artworkMapper.countSearchResults(params);

        Map<String, Object> result = new HashMap<>();
        result.put("artworks", artworks);
        result.put("total", total);
        result.put("page", searchRequest.getPage());
        result.put("pageSize", searchRequest.getPageSize());
        result.put("totalPages", (int) Math.ceil((double) total / searchRequest.getPageSize()));

        return result;
    }

    /**
     * 获取搜索建议
     */
    public List<String> getSearchSuggestions(String keyword) {
        return searchLogMapper.getKeywordSuggestions(keyword, 10);
    }

    /**
     * 获取热门搜索词
     */
    public List<Map<String, Object>> getHotKeywords(int days, int limit) {
        return searchLogMapper.getHotKeywords(days, limit);
    }

    /**
     * 获取用户搜索历史
     */
    public List<SearchLog> getUserSearchHistory(int page, int pageSize) {
        Integer userId = getCurrentUserIdSafely();
        if (userId == null) {
            return List.of();
        }

        int offset = (page - 1) * pageSize;
        return searchLogMapper.getUserSearchHistory(userId, offset, pageSize);
    }

    /**
     * 构建搜索参数
     */
    private Map<String, Object> buildSearchParams(SearchRequest searchRequest) {
        Map<String, Object> params = new HashMap<>();

        // 关键词搜索
        if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().trim().isEmpty()) {
            params.put("keyword", "%" + searchRequest.getKeyword().trim() + "%");
        }

        // 分类筛选
        if (searchRequest.getCategoryId() != null) {
            params.put("categoryId", searchRequest.getCategoryId());
        }

        // 标签筛选
        if (searchRequest.getTags() != null && !searchRequest.getTags().isEmpty()) {
            params.put("tags", searchRequest.getTags());
        }

        // 价格范围筛选
        if (searchRequest.getPriceRange() != null && !searchRequest.getPriceRange().isEmpty()) {
            String[] priceRange = searchRequest.getPriceRange().split("-");
            if (priceRange.length == 2) {
                try {
                    params.put("minPrice", new BigDecimal(priceRange[0]));
                    params.put("maxPrice", new BigDecimal(priceRange[1]));
                } catch (NumberFormatException e) {
                    // 忽略无效的价格范围
                }
            }
        }

        // 排序
        String sortBy = searchRequest.getSortBy();
        if (sortBy != null) {
            switch (sortBy) {
                case "price_asc":
                    params.put("orderBy", "a.price ASC");
                    break;
                case "price_desc":
                    params.put("orderBy", "a.price DESC");
                    break;
                case "time_desc":
                    params.put("orderBy", "a.upload_time DESC");
                    break;
                case "popular":
                    params.put("orderBy", "a.view_count DESC, a.like_count DESC");
                    break;
                default:
                    params.put("orderBy", "a.upload_time DESC");
            }
        } else {
            params.put("orderBy", "a.upload_time DESC");
        }

        // 分页
        int offset = (searchRequest.getPage() - 1) * searchRequest.getPageSize();
        params.put("offset", offset);
        params.put("pageSize", searchRequest.getPageSize());

        return params;
    }

    /**
     * 记录搜索日志
     */
    private void recordSearchLog(String keyword, int searchType) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        SearchLog searchLog = new SearchLog();
        searchLog.setUserId(getCurrentUserIdSafely());
        searchLog.setKeyword(keyword.trim());
        searchLog.setSearchType(searchType);
        searchLog.setIpAddress(getClientIpAddress());
        searchLog.setUserAgent(request.getHeader("User-Agent"));

        searchLogMapper.insert(searchLog);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress() {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * 安全获取当前用户ID
     */
    private Integer getCurrentUserIdSafely() {
        try {
            return UserContext.getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}