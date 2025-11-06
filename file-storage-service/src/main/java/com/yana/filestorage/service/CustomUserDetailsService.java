package com.yana.filestorage.service;

import com.yana.filestorage.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Здесь нужно получить пользователя через Feign client из user-service
        // Пока заглушка
        return UserDetailsImpl.builder()
                .email(email)
                .password("") // Пароль не используется при JWT аутентификации
                .build();
    }
}
