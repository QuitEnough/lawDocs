package com.yana.userservice.service;

import com.yana.userservice.dto.UserCreateRequest;
import com.yana.userservice.dto.UserResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    void addUser(UserCreateRequest request);

    UserResponse getUserById(long userId);

    UserResponse getUserByEmail(String email);

    List<?> getUserByValue(String value);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}