package com.newer.jay.demo.service.user;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.newer.jay.demo.dto.LoginResponseDTO;
import com.newer.jay.demo.dto.UserDTO;
import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.mapper.UserMapper;

import jakarta.annotation.Resource;

@Service
public class UserInfoService {
    @Resource
    private UserMapper userMapper;
    public LoginResponseDTO UserInfo() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailImpl loginUser = (UserDetailImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        UserDTO userDTO = new UserDTO();
        LoginResponseDTO userInfo = new LoginResponseDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setAvatarFileName(user.getAvatarFileName());
        userDTO.setMemberLevel(user.getMemberLevel());
        userDTO.setMemberPoints(user.getMemberPoints());
        userDTO.setJoinDate(user.getJoinDate());
        userDTO.setTotalOrders(user.getTotalOrders());
        userDTO.setBalance(user.getBalance());
        userDTO.setIdCard(user.getIdCard());
        userDTO.setAddress(user.getAddress());

        userInfo.setStatus(200);
        userInfo.setMessage("OK");
        userInfo.setToken(null); // No token needed for user info retrieval
        userInfo.setData(userDTO);
        return userInfo;
    }
}
