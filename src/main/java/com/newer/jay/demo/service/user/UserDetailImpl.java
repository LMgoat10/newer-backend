package com.newer.jay.demo.service.user;

import com.newer.jay.demo.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class UserDetailImpl implements UserDetails {
    private User user;

    // 无参构造函数
    public UserDetailImpl() {
    }

    // 有参构造函数
    public UserDetailImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 为用户提供ROLE_USER权限
        return java.util.Collections.singletonList(
            new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
        );
    }

    @Override
    public String getPassword() {
        return user.getHashedPassword();
    }

    @Override
    public String getUsername() { 
        return user.getEmail(); 
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

