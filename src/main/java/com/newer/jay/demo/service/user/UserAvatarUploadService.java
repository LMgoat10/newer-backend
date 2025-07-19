package com.newer.jay.demo.service.user;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserAvatarUploadService {
    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param avatarFile 头像文件
     * @return 上传结果
     */
    @Value("${upload.path}")
    private String uploadPath;

    public Map<String, Object> uploadAvatar(MultipartFile avatar) throws IOException {
        // 验证文件是否为空
        if (avatar.isEmpty()) {
            return Map.of("status", 1, "message", "上传的文件为空");
        }
        
        String originalFilename = avatar.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Map.of("status", 1, "message", "文件名无效");
        }
        
        // 验证文件扩展名
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        boolean isValidExtension = false;
        for (String ext : allowedExtensions) {
            if (suffix.equals(ext)) {
                isValidExtension = true;
                break;
            }
        }
        
        if (!isValidExtension) {
            return Map.of("status", 1, "message", "不支持的文件格式，仅支持: jpg, jpeg, png, gif, bmp");
        }
        
        String fileName = UUID.randomUUID() + suffix;

        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String savePath = uploadPath + fileName;
        File dest = new File(savePath);
        
        try {
            avatar.transferTo(dest);
            return Map.of("status", 0, "message", "上传成功", "fileName", fileName);
        } catch (IOException e) {
            return Map.of("status", 1, "message", "文件保存失败: " + e.getMessage());
        }
    }
}

