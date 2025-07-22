package com.newer.jay.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.newer.jay.demo.util.MailUtils;
import com.newer.jay.demo.service.VerificationCodeService;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "邮件管理")
@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private VerificationCodeService verificationCodeService;

    // ==================== 测试接口 ====================

    /**
     * 发送简单测试邮件
     */
    @ApiOperation("发送简单测试邮件")
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> sendTestMail() {
        Map<String, Object> response = new HashMap<>();
        try {
            mailUtils.sendSimpleMail("ruiyeclub@foxmail.com", "普通文本邮件", "普通文本邮件内容");
            response.put("success", true);
            response.put("message", "测试邮件发送成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 发送HTML测试邮件
     */
    @ApiOperation("发送HTML测试邮件")
    @PostMapping("/sendHtml")
    public ResponseEntity<Map<String, Object>> sendHtmlMail() {
        Map<String, Object> response = new HashMap<>();
        try {
            mailUtils.sendHtmlMail("xiyan616@gmail.com", "HTML测试邮件",
                    "<div style=\"text-align: center;\">" +
                            "<h3>HTML测试邮件</h3>" +
                            "<div>这是一封HTML格式的测试邮件</div>" +
                            "</div>");
            response.put("success", true);
            response.put("message", "HTML邮件发送成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ==================== 验证码相关接口 ====================

    /**
     * 发送邮箱验证码
     * @param email 邮箱地址
     * @param purpose 验证码用途：register(注册)、login(登录)、reset_password(重置密码)、change_email(更换邮箱)
     */
    @ApiOperation("发送邮箱验证码")
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(
            @RequestParam String email,
            @RequestParam(defaultValue = "register") String purpose) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 验证邮箱格式
            if (!isValidEmail(email)) {
                response.put("success", false);
                response.put("message", "邮箱格式不正确");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 2. 检查发送频率限制
            if (!verificationCodeService.canSendCode(email)) {
                long remainingTime = verificationCodeService.getRemainingWaitTime(email);
                response.put("success", false);
                response.put("message", "发送过于频繁，请" + remainingTime + "秒后再试");
                response.put("remainingTime", remainingTime);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 3. 生成验证码
            String code = verificationCodeService.generateCode();
            
            // 4. 发送邮件
            mailUtils.sendVerificationCode(email, code, purpose);
            
            // 5. 存储验证码和设置发送间隔
            verificationCodeService.storeCode(email, code);
            verificationCodeService.setSendInterval(email);
            
            response.put("success", true);
            response.put("message", "验证码发送成功，请查收邮件");
            response.put("email", email);
            response.put("purpose", purpose);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 验证邮箱验证码
     * @param email 邮箱地址
     * @param code 验证码
     */
    @ApiOperation("验证邮箱验证码")
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(
            @RequestParam String email,
            @RequestParam String code) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 验证邮箱格式
            if (!isValidEmail(email)) {
                response.put("success", false);
                response.put("message", "邮箱格式不正确");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 2. 验证码格式检查
            if (code == null || code.trim().length() != 6) {
                response.put("success", false);
                response.put("message", "验证码格式不正确");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 3. 验证验证码
            boolean isValid = verificationCodeService.verifyCode(email, code);
            
            if (isValid) {
                response.put("success", true);
                response.put("message", "验证成功");
                response.put("email", email);
            } else {
                response.put("success", false);
                response.put("message", "验证码错误或已过期");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "验证失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 检查验证码状态
     * @param email 邮箱地址
     */
    @ApiOperation("检查验证码状态")
    @GetMapping("/check-code-status")
    public ResponseEntity<Map<String, Object>> checkCodeStatus(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean hasValidCode = verificationCodeService.hasValidCode(email);
            boolean canSend = verificationCodeService.canSendCode(email);
            long remainingTime = verificationCodeService.getRemainingWaitTime(email);
            
            response.put("success", true);
            response.put("email", email);
            response.put("hasValidCode", hasValidCode);
            response.put("canSendNewCode", canSend);
            response.put("remainingWaitTime", remainingTime);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ==================== 其他邮件功能 ====================

    @ApiOperation("发送html模板邮件")
    @PostMapping("/sendTemplate")
    public ResponseEntity<Map<String, Object>> sendTemplate() {
        Map<String, Object> response = new HashMap<>();
        try {
            mailUtils.sendTemplateMail("ruiyeclub@foxmail.com", "基于模板的html邮件", "hello.html");
            response.put("success", true);
            response.put("message", "模板邮件发送成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @ApiOperation("发送带附件的邮件")
    @GetMapping("/sendAttachmentsMail")
    public ResponseEntity<Map<String, Object>> sendAttachmentsMail() {
        Map<String, Object> response = new HashMap<>();
        try {
            String filePath = "D:\\projects\\springboot\\template.png";
            mailUtils.sendAttachmentsMail("xxxx@xx.com", "带附件的邮件", "邮件中有附件", filePath);
            response.put("success", true);
            response.put("message", "附件邮件发送成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "发送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
