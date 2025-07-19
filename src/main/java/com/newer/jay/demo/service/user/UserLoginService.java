package com.newer.jay.demo.service.user;

import com.newer.jay.demo.entity.User;
import com.newer.jay.demo.util.JwtUtil;
import com.newer.jay.demo.dto.LoginResponseDTO;
import com.newer.jay.demo.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Slf4j
@Service
public class UserLoginService {
    @Resource
    private AuthenticationManager authenticationManager;

    public LoginResponseDTO login(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        UserDetailImpl loginUser = (UserDetailImpl) authenticate.getPrincipal();

        User user = loginUser.getUser();
        String jwt = JwtUtil.createJWT(user.getUserId().toString());
        UserDTO userDTO = new UserDTO();
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

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setStatus(200);
        responseDTO.setMessage("OK");
        responseDTO.setToken(jwt);
        responseDTO.setData(userDTO);

        return responseDTO;
    }
}