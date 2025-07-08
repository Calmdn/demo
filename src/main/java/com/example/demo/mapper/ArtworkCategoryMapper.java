package com.example.demo.mapper;

import com.example.demo.pojo.ArtworkCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArtworkCategoryMapper {

    /**
     * 获取所有启用的分类
     */
    List<ArtworkCategory> selectAllEnabled();

    /**
     * 根据ID获取分类
     */
    ArtworkCategory selectById(Integer id);
}