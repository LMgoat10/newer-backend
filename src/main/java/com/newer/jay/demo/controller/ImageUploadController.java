package com.newer.jay.demo.controller;

import com.newer.jay.demo.service.ImageUploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class ImageUploadController {
    
    @Resource
    private ImageUploadService imageUploadService;

    @PostMapping("/api/upload/image")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        if (file == null || file.isEmpty()) {
            response.setStatus(400);
            return Map.of("status", 1, "message", "请选择要上传的图片文件");
        }

        try {
            return imageUploadService.uploadImage(file);
        } catch (Exception e) {
            response.setStatus(500);
            return Map.of("status", 1, "message", "上传失败: " + e.getMessage());
        }
    }
}
