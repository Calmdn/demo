package com.example.demo.mapper;

import com.example.demo.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {

    // 插入标签
    void insert(Tag tag);

    // 根据名称查找标签
    Tag findByName(String name);

    // 批量插入作品标签关联
    void batchInsertArtworkTags(@Param("artworkId") Integer artworkId, @Param("tagIds") List<Integer> tagIds);

    // 删除作品的所有标签
    void deleteArtworkTags(Integer artworkId);

    // 获取作品的标签
    List<Tag> getArtworkTags(Integer artworkId);

    // 获取热门标签
    List<Tag> getHotTags(int limit);

    // 更新标签使用次数
    void incrementUsageCount(@Param("tagId") Integer tagId, @Param("increment") int increment);

    // 搜索标签
    List<Tag> searchTags(@Param("keyword") String keyword, @Param("limit") int limit);
}