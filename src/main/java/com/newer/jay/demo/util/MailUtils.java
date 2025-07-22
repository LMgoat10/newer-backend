package com.newer.jay.demo.util;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MailUtils{

    /**
     * Spring官方提供的集成邮件服务的实现类，目前是Java后端发送邮件和集成邮件服务的主流工具。
     */
    @Resource
    private JavaMailSender mailSender;

    /**
     * 从配置文件中注入发件人的姓名
     */
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * 发送文本邮件
     * @param to      收件人
     * @param subject 标题
     * @param content 正文
     */
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        //发件人
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    /**
     * 发送html邮件
     */
    public void sendHtmlMail(String to, String subject, String content) {
        try {
            //注意这里使用的是MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            //第二个参数：格式是否为html
            helper.setText(content, true);
            mailSender.send(message);
        }catch (MessagingException e){
            log.error("发送邮件时发生异常！", e);
        }
    }

    /**
     * 发送模板邮件
     * @param to
     * @param subject
     * @param template
     */
    public void sendTemplateMail(String to, String subject, String template){
        try {
            // 获得模板
            Template template1 = freeMarkerConfigurer.getConfiguration().getTemplate(template);
            // 使用Map作为数据模型，定义属性和值
            Map<String,Object> model = new HashMap<>();
            model.put("myname","Ray。");
            // 传入数据模型到模板，替代模板中的占位符，并将模板转化为html字符串
            String templateHtml = FreeMarkerTemplateUtils.processTemplateIntoString(template1,model);
            // 该方法本质上还是发送html邮件，调用之前发送html邮件的方法
            this.sendHtmlMail(to, subject, templateHtml);
        } catch (TemplateException e) {
            log.error("发送邮件时发生异常！", e);
        } catch (IOException e) {
            log.error("发送邮件时发生异常！", e);
        }
    }

    /**
     * 发送带附件的邮件
     * @param to
     * @param subject
     * @param content
     * @param filePath
     */
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            //要带附件第二个参数设为true
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);
            mailSender.send(message);
        }catch (MessagingException e){
            log.error("发送邮件时发生异常！", e);
        }

    }

    /**
     * 发送验证码邮件
     * @param to 收件人邮箱
     * @param code 验证码
     * @param purpose 验证码用途（注册、登录、找回密码等）
     */
    public void sendVerificationCode(String to, String code, String purpose) {
        try {
            String subject = getCodeEmailSubject(purpose);
            String content = getCodeEmailContent(code, purpose);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
            log.info("验证码邮件发送成功: to={}, purpose={}", to, purpose);
            
        } catch (MessagingException e) {
            log.error("发送验证码邮件失败: to={}, purpose={}", to, purpose, e);
            throw new RuntimeException("发送验证码邮件失败", e);
        }
    }

    /**
     * 根据用途获取邮件主题
     * @param purpose 验证码用途
     * @return 邮件主题
     */
    private String getCodeEmailSubject(String purpose) {
        switch (purpose.toLowerCase()) {
            case "register":
                return "【旅游系统】注册验证码";
            case "login":
                return "【旅游系统】登录验证码";
            case "reset_password":
                return "【旅游系统】密码重置验证码";
            case "change_email":
                return "【旅游系统】邮箱变更验证码";
            default:
                return "【旅游系统】验证码";
        }
    }

    /**
     * 根据用途获取邮件内容
     * @param code 验证码
     * @param purpose 验证码用途
     * @return HTML格式的邮件内容
     */
    private String getCodeEmailContent(String code, String purpose) {
        String actionText = getActionText(purpose);
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>验证码邮件</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px 20px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "        .content { background: #f9f9f9; padding: 30px 20px; border-radius: 0 0 10px 10px; }" +
                "        .code-box { background: #ffffff; border: 2px dashed #667eea; padding: 20px; margin: 20px 0; text-align: center; border-radius: 8px; }" +
                "        .code { font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 5px; font-family: 'Courier New', monospace; }" +
                "        .warning { background: #fff3cd; border: 1px solid #ffeaa7; color: #856404; padding: 15px; border-radius: 5px; margin: 20px 0; }" +
                "        .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }" +
                "        .highlight { color: #667eea; font-weight: bold; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"header\">" +
                "        <h1>🎫 旅游预订系统</h1>" +
                "        <p>您的" + actionText + "验证码</p>" +
                "    </div>" +
                "    <div class=\"content\">" +
                "        <p>尊敬的用户，您好！</p>" +
                "        <p>您正在进行<span class=\"highlight\">" + actionText + "</span>操作，请使用以下验证码完成验证：</p>" +
                "        <div class=\"code-box\">" +
                "            <div class=\"code\">" + code + "</div>" +
                "        </div>" +
                "        <div class=\"warning\">" +
                "            <strong>⚠️ 安全提醒：</strong><br>" +
                "            • 验证码有效期为 <strong>5分钟</strong><br>" +
                "            • 请勿将验证码告诉他人<br>" +
                "            • 如非本人操作，请忽略此邮件" +
                "        </div>" +
                "        <p>如果您有任何疑问，请联系我们的客服团队。</p>" +
                "        <p>感谢您使用我们的服务！</p>" +
                "    </div>" +
                "    <div class=\"footer\">" +
                "        <p>此邮件由系统自动发送，请勿回复</p>" +
                "        <p>© 2024 旅游预订系统 保留所有权利</p>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 根据用途获取操作文本
     * @param purpose 验证码用途
     * @return 操作文本
     */
    private String getActionText(String purpose) {
        switch (purpose.toLowerCase()) {
            case "register":
                return "账户注册";
            case "login":
                return "安全登录";
            case "reset_password":
                return "密码重置";
            case "change_email":
                return "邮箱变更";
            default:
                return "身份验证";
        }
    }
}
