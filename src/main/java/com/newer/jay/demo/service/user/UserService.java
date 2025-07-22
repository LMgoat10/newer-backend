package com.newer.jay.demo.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.newer.jay.demo.dto.LoginResponseDTO;
import com.newer.jay.demo.dto.UserDTO;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.mapper.UserMapper;
import com.newer.jay.demo.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 用户服务 - 整合所有用户相关的业务逻辑
 * 包括：用户信息、登录、注册、更新、头像上传
 */
@Slf4j
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Value("${upload.path}"+"userAvatar\\")
    private String uploadPath;

    /**
     * 获取当前登录用户信息
     * @return 用户信息响应
     */
    public LoginResponseDTO getUserInfo() {
        UsernamePasswordAuthenticationToken authenticationToken = 
            (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailImpl loginUser = (UserDetailImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        
        UserDTO userDTO = convertToUserDTO(user);
        
        LoginResponseDTO userInfo = new LoginResponseDTO();
        userInfo.setStatus(200);
        userInfo.setMessage("OK");
        userInfo.setToken(null); // No token needed for user info retrieval
        userInfo.setData(userDTO);
        return userInfo;
    }

    /**
     * 用户登录
     * @param email 邮箱
     * @param password 密码
     * @return 登录响应
     */
    public LoginResponseDTO login(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(email, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        UserDetailImpl loginUser = (UserDetailImpl) authenticate.getPrincipal();

        User user = loginUser.getUser();
        String jwt = JwtUtil.createJWT(user.getUserId().toString());
        UserDTO userDTO = convertToUserDTO(user);

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setStatus(200);
        responseDTO.setMessage("OK");
        responseDTO.setToken(jwt);
        responseDTO.setData(userDTO);

        return responseDTO;
    }

    /**
     * 用户注册服务
     * @param name 用户名
     * @param email 邮箱
     * @param password 密码
     * @param phone 手机号码（可选）
     * @param avatarFileName 头像文件名（可选）
     * @return 注册结果
     */
    public Map<String, Object> register(String name, String email, String password, String phone, String avatarFileName) {
        try {
            // 验证输入参数
            if (name == null || name.trim().isEmpty()) {
                return Map.of("status", 1, "message", "用户名不能为空");
            }
            if (email == null || email.trim().isEmpty()) {
                return Map.of("status", 1, "message", "邮箱不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                return Map.of("status", 1, "message", "密码不能为空");
            }
            
            // 验证邮箱格式
            if (!isValidEmail(email)) {
                return Map.of("status", 1, "message", "邮箱格式不正确");
            }
            
            // 验证密码强度
            if (password.length() < 6) {
                return Map.of("status", 1, "message", "密码长度至少6位");
            }

            // 检查邮箱是否已存在
            if (!isEmailAvailable(email)) {
                return Map.of("status", 1, "message", "邮箱已被注册");
            }

            // 检查用户名是否已存在
            if (!isUsernameAvailable(name)) {
                return Map.of("status", 1, "message", "用户名已被占用");
            }

            // 加密密码
            String encodedPassword = passwordEncoder.encode(password);

            // 创建新用户
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setHashedPassword(encodedPassword);
            user.setAvatarFileName(avatarFileName);
            user.setMemberLevel("普通会员");
            user.setJoinDate(new Date());
            user.setTotalOrders(0);
            user.setPoints(0);
            user.setBalance(0.0);

            // 保存用户
            int result = userMapper.insert(user);
            
            if (result > 0) {
                return Map.of(
                    "status", 0, 
                    "message", "注册成功", 
                    "userId", user.getUserId(),
                    "name", user.getName(),
                    "email", user.getEmail()
                );
            } else {
                return Map.of("status", 1, "message", "注册失败，请稍后重试");
            }

        } catch (Exception e) {
            log.error("用户注册异常: {}", e.getMessage(), e);
            return Map.of("status", 1, "message", "系统异常，注册失败");
        }
    }

    /**
     * 检查用户名是否可用
     * @param name 用户名
     * @return 是否可用
     */
    public boolean isUsernameAvailable(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("name", name);
        return userMapper.selectCount(query) == 0;
    }

    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email", email);
        return userMapper.selectCount(query) == 0;
    }

    /**
     * 更新用户信息
     * @param name 用户名
     * @param email 邮箱
     * @param password 密码
     * @param phone 手机号
     * @param avatarFileName 头像文件名
     * @return 更新结果
     */
    public Map<String, Object> updateUser(String name, String email, String password, String phone, String avatarFileName) {
        UsernamePasswordAuthenticationToken authenticationToken = 
            (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailImpl loginUser = (UserDetailImpl) authenticationToken.getPrincipal();
        
        try {    
            // 验证邮箱格式
            if (email != null && !email.isEmpty() && !isValidEmail(email)) {
                return Map.of("status", 1, "message", "邮箱格式不正确");
            }
            
            // 验证密码强度
            if (password != null && !password.isEmpty() && password.length() < 6) {
                return Map.of("status", 1, "message", "密码长度至少6位");
            }

            // 检查邮箱是否已存在（排除当前用户）
            if (email != null && !email.isEmpty()) {
                QueryWrapper<User> emailQuery = new QueryWrapper<>();
                emailQuery.eq("email", email);
                emailQuery.ne("user_id", loginUser.getUser().getUserId());
                if (userMapper.selectCount(emailQuery) > 0) {
                    return Map.of("status", 1, "message", "邮箱已被注册");
                }
            }

            // 检查用户名是否已存在（排除当前用户）
            if (name != null && !name.isEmpty()) {
                QueryWrapper<User> nameQuery = new QueryWrapper<>();
                nameQuery.eq("name", name);
                nameQuery.ne("user_id", loginUser.getUser().getUserId());
                if (userMapper.selectCount(nameQuery) > 0) {
                    return Map.of("status", 1, "message", "用户名已被占用");
                }
            }

            // 获取当前用户信息
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", loginUser.getUser().getUserId());
            User user = userMapper.selectOne(queryWrapper);
            
            // 更新用户信息
            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }
            if (email != null && !email.isEmpty()) {
                user.setEmail(email);
            }
            if (phone != null && !phone.isEmpty()) {
                user.setPhone(phone);
            }
            if (password != null && !password.isEmpty()) {
                user.setHashedPassword(passwordEncoder.encode(password));
            }
            if (avatarFileName != null && !avatarFileName.isEmpty()) {
                user.setAvatarFileName(avatarFileName);
            }
            
            if (userMapper.updateById(user) > 0) {
                return Map.of(
                    "status", 0, 
                    "message", "更新成功", 
                    "userId", user.getUserId(),
                    "name", user.getName(),
                    "email", user.getEmail()
                );
            } else {
                return Map.of("status", 1, "message", "更新失败，请稍后重试");
            }

        } catch (Exception e) {
            log.error("用户更新异常: {}", e.getMessage(), e);
            return Map.of("status", 1, "message", "系统异常，更新失败");
        }
    }

    /**
     * 上传用户头像
     * @param avatar 头像文件
     * @return 上传结果
     */
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

    /**
     * 将User实体转换为UserDTO
     * @param user User实体
     * @return UserDTO
     */
    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setAvatarFileName(user.getAvatarFileName());
        userDTO.setMemberLevel(user.getMemberLevel());
        userDTO.setJoinDate(user.getJoinDate());
        userDTO.setTotalOrders(user.getTotalOrders());
        userDTO.setPoints(user.getPoints());
        userDTO.setBalance(user.getBalance());
        return userDTO;
    }

    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
