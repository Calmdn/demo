package com.example.demo.service;

import com.example.demo.mapper.TagMapper;
import com.example.demo.pojo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    /**
     * 为作品设置标签
     */
    @Transactional
    public void setArtworkTags(Integer artworkId, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        // 删除现有标签关联
        tagMapper.deleteArtworkTags(artworkId);

        List<Integer> tagIds = new ArrayList<>();

        for (String tagName : tagNames) {
            if (tagName == null || tagName.trim().isEmpty()) {
                continue;
            }

            tagName = tagName.trim();

            // 查找或创建标签
            Tag tag = tagMapper.findByName(tagName);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
                tag.setStatus(1);
                tagMapper.insert(tag);
            }

            tagIds.add(tag.getId());

            // 增加使用次数
            tagMapper.incrementUsageCount(tag.getId(), 1);
        }

        if (!tagIds.isEmpty()) {
            tagMapper.batchInsertArtworkTags(artworkId, tagIds);
        }
    }

    /**
     * 获取作品标签
     */
    public List<Tag> getArtworkTags(Integer artworkId) {
        return tagMapper.getArtworkTags(artworkId);
    }

    /**
     * 获取热门标签
     */
    public List<Tag> getHotTags(int limit) {
        return tagMapper.getHotTags(limit);
    }

    /**
     * 搜索标签
     */
    public List<Tag> searchTags(String keyword, int limit) {
        return tagMapper.searchTags(keyword, limit);
    }
}