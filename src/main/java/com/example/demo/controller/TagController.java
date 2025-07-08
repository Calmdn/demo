package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.pojo.Tag;
import com.example.demo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取作品标签
     */
    @GetMapping("/artwork/{artworkId}")
    public Result<List<Tag>> getArtworkTags(@PathVariable Integer artworkId) {
        List<Tag> tags = tagService.getArtworkTags(artworkId);
        return Result.success(tags);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    public Result<List<Tag>> getHotTags(@RequestParam(defaultValue = "20") int limit) {
        List<Tag> tags = tagService.getHotTags(limit);
        return Result.success(tags);
    }

    /**
     * 搜索标签
     */
    @GetMapping("/search")
    public Result<List<Tag>> searchTags(@RequestParam String keyword,
                                        @RequestParam(defaultValue = "10") int limit) {
        List<Tag> tags = tagService.searchTags(keyword, limit);
        return Result.success(tags);
    }
}