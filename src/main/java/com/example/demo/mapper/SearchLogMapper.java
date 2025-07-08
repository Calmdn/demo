package com.example.demo.mapper;

import com.example.demo.pojo.SearchLog;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface SearchLogMapper {

    // 插入搜索记录
    void insert(SearchLog searchLog);

    // 获取热门搜索词
    @MapKey("keyword")
    List<Map<String, Object>> getHotKeywords(@Param("days") int days, @Param("limit") int limit);

    // 获取用户搜索历史
    List<SearchLog> getUserSearchHistory(@Param("userId") Integer userId,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);

    // 搜索关键词建议
    List<String> getKeywordSuggestions(@Param("keyword") String keyword, @Param("limit") int limit);

    int countSearchesByDate(@Param("date") LocalDate date);
    // 统计用户月度搜索次数
    int countUserSearchesByMonth(@Param("userId") Integer userId, @Param("statMonth") String statMonth);
}