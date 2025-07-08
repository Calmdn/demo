package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 上传作品图片
     */
    @PostMapping("/artwork")
    public Result<String> uploadArtwork(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileUploadUtil.uploadFile(file);
            return Result.success("上传成功", imageUrl);
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}