package com.example.demo.service;

import com.example.demo.mapper.ArtworkCategoryMapper;
import com.example.demo.pojo.ArtworkCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtworkCategoryService {

    @Autowired
    private ArtworkCategoryMapper categoryMapper;

    /**
     * 获取所有分类
     */
    public List<ArtworkCategory> getAllCategories() {
        return categoryMapper.selectAllEnabled();
    }

    /**
     * 根据ID获取分类
     */
    public ArtworkCategory getCategoryById(Integer id) {
        return categoryMapper.selectById(id);
    }
}