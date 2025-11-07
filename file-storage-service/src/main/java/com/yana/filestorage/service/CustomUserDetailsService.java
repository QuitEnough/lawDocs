package com.yana.filestorage.service;

import com.yana.filestorage.dto.UserInfo;
import com.yana.filestorage.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final FileService fileService;
    private final RestTemplate restTemplate;

    @Value("${user.service.url:http://user-service:8084}")
    private String userServiceUrl;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            // Для JWT аутентификации достаточно базовой информации
            // В реальном сценарии можно сделать запрос к user-service для проверки пользователя
            return UserDetailsImpl.builder()
                    .email(email)
                    .password("") // Пароль не проверяется при JWT
                    .role("USER")
                    .build();

        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }

    public boolean isFileOwner(Long userId, Long fileId, String authToken) {
        try {
            // 1. Проверяем существование пользователя через user-service
            String userExistsUrl = userServiceUrl + "/users/" + userId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Пытаемся получить информацию о пользователе
            ResponseEntity<Object> userResponse = restTemplate.exchange(
                    userExistsUrl,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            // Если пользователь не существует (404 или другая ошибка), будет исключение
            // и мы попадем в catch блок

            // 2. Проверяем владение файлом в локальной БД file-storage-service
            return fileService.isFileOwner(fileId, userId);

        } catch (Exception e) {
            // Если произошла ошибка при обращении к user-service или пользователь не найден
            return false;
        }
    }

    // Метод для получения информации о пользователе
    public Optional<UserInfo> getUserInfo(Long userId, String authToken) {
        try {
            String url = userServiceUrl + "/users?id=" + userId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<UserInfo> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    UserInfo.class
            );

            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
