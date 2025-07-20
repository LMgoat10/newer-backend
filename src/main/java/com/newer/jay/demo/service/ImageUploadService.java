package com.newer.jay.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageUploadService {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url}")
    private String uploadUrl;

    public Map<String, Object> uploadImage(MultipartFile image) throws IOException {
        // 检查文件类型
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", 1);
            result.put("message", "文件格式不正确，请上传图片文件");
            return result;
        }

        // 检查文件大小 (限制为5MB)
        if (image.getSize() > 5 * 1024 * 1024) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", 1);
            result.put("message", "图片大小不能超过5MB");
            return result;
        }

        // 生成唯一文件名
        String originalFilename = image.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 保存文件
        String savePath = uploadPath + fileName;
        image.transferTo(new File(savePath));

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("status", 0);
        result.put("message", "上传成功");
        result.put("url", uploadUrl + fileName);
        result.put("fileName", fileName);
        return result;
    }
}
