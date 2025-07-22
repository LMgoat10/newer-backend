package com.newer.jay.demo.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务类（内存版本）
 */
@Service
@Slf4j
public class VerificationCodeService {

    // 验证码长度
    private static final int CODE_LENGTH = 6;
    
    // 验证码有效期（毫秒）
    private static final long CODE_EXPIRE_MILLIS = 5 * 60 * 1000; // 5分钟
    
    // 发送间隔限制（毫秒）
    private static final long SEND_INTERVAL_MILLIS = 60 * 1000; // 60秒

    // 内存存储验证码信息
    private static class CodeInfo {
        String code;
        long expireTime;
        
        CodeInfo(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    // 存储验证码
    private final ConcurrentHashMap<String, CodeInfo> codeStore = new ConcurrentHashMap<>();
    
    // 存储发送时间限制
    private final ConcurrentHashMap<String, Long> sendTimeStore = new ConcurrentHashMap<>();

    /**
     * 生成6位数字验证码
     * @return 验证码
     */
    public String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }

    /**
     * 存储验证码到内存
     * @param email 邮箱地址
     * @param code 验证码
     */
    public void storeCode(String email, String code) {
        long expireTime = System.currentTimeMillis() + CODE_EXPIRE_MILLIS;
        codeStore.put(email, new CodeInfo(code, expireTime));
        log.info("验证码已存储: email={}, code={}, 有效期=5分钟", email, code);
        
        // 清理过期的验证码
        cleanExpiredCodes();
    }

    /**
     * 验证验证码
     * @param email 邮箱地址
     * @param inputCode 用户输入的验证码
     * @return 验证结果
     */
    public boolean verifyCode(String email, String inputCode) {
        if (email == null || inputCode == null) {
            return false;
        }
        
        CodeInfo codeInfo = codeStore.get(email);
        
        if (codeInfo == null || codeInfo.isExpired()) {
            if (codeInfo != null) {
                codeStore.remove(email);
            }
            log.warn("验证码已过期或不存在: email={}", email);
            return false;
        }
        
        boolean isValid = codeInfo.code.equals(inputCode.trim());
        
        if (isValid) {
            // 验证成功后删除验证码，防止重复使用
            codeStore.remove(email);
            log.info("验证码验证成功: email={}", email);
        } else {
            log.warn("验证码验证失败: email={}, input={}, stored={}", email, inputCode, codeInfo.code);
        }
        
        return isValid;
    }

    /**
     * 检查是否可以发送验证码（防止频繁发送）
     * @param email 邮箱地址
     * @return 是否可以发送
     */
    public boolean canSendCode(String email) {
        Long lastSendTime = sendTimeStore.get(email);
        if (lastSendTime == null) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastSendTime) >= SEND_INTERVAL_MILLIS;
    }

    /**
     * 设置发送间隔限制
     * @param email 邮箱地址
     */
    public void setSendInterval(String email) {
        sendTimeStore.put(email, System.currentTimeMillis());
        
        // 清理过期的发送时间记录
        cleanExpiredSendTimes();
    }

    /**
     * 获取剩余等待时间（秒）
     * @param email 邮箱地址
     * @return 剩余秒数
     */
    public long getRemainingWaitTime(String email) {
        Long lastSendTime = sendTimeStore.get(email);
        if (lastSendTime == null) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastSendTime;
        long remainingMillis = SEND_INTERVAL_MILLIS - elapsedTime;
        
        return remainingMillis > 0 ? TimeUnit.MILLISECONDS.toSeconds(remainingMillis) : 0;
    }

    /**
     * 清除验证码（用于特殊情况）
     * @param email 邮箱地址
     */
    public void clearCode(String email) {
        codeStore.remove(email);
        log.info("验证码已清除: email={}", email);
    }

    /**
     * 清理过期的验证码
     */
    private void cleanExpiredCodes() {
        codeStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * 清理过期的发送时间记录
     */
    private void cleanExpiredSendTimes() {
        long currentTime = System.currentTimeMillis();
        sendTimeStore.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) > SEND_INTERVAL_MILLIS);
    }

    /**
     * 检查验证码是否存在且未过期
     * @param email 邮箱地址
     * @return 是否存在有效验证码
     */
    public boolean hasValidCode(String email) {
        CodeInfo codeInfo = codeStore.get(email);
        return codeInfo != null && !codeInfo.isExpired();
    }
}
