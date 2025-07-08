package com.example.demo.utils;

import com.example.demo.config.FileUploadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class FileUploadUtil {

    @Autowired
    private FileUploadConfig fileUploadConfig;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 检查文件
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (!isImageFile(originalFilename)) {
            throw new RuntimeException("只能上传图片文件");
        }

        // 生成新文件名：时间戳_UUID.扩展名
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = getFileExtension(originalFilename);
        String newFileName = timeStamp + "_" + uuid + "." + extension;

        // 创建日期目录
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uploadDir = System.getProperty("user.dir") + fileUploadConfig.getPath() + dateDir;

        // 确保目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        File targetFile = new File(uploadDir, newFileName);
        file.transferTo(targetFile);

        // 返回访问URL
        return fileUploadConfig.getDomain() + fileUploadConfig.getPath() + dateDir + "/" + newFileName;
    }

    /**
     * 检查是否为图片文件
     */
    private boolean isImageFile(String filename) {
        if (filename == null) return false;
        String extension = getFileExtension(filename).toLowerCase();
        return extension.matches("jpg|jpeg|png|gif|bmp|webp");
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }
}