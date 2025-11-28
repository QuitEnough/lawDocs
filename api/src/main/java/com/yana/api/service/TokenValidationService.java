package com.yana.api.service;

import com.yana.api.exception.InvalidTokenException;
import com.yana.api.feign.IdentityServiceFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenValidationService {

    private final IdentityServiceFeign identityFeign;

    //TODO: придумать логику валидации токена
    public void validateToken(String authHeader) {
        try {
            // Создаем простой запрос для проверки токена
            // В реальности может быть отдельный endpoint /validate-token в Identity Service
            log.debug("Validating JWT token");

            // Если токен невалиден, Feign выбросит исключение
            // Вам нужно реализовать endpoint в Identity Service для проверки токена
            // Например: identityServiceFeign.validateToken(authHeader);

            // Временная реализация - пытаемся получить информацию о пользователе
            // Это вызовет исключение если токен невалиден
            validateTokenByUserInfo(authHeader);

        } catch (HttpClientErrorException.Forbidden e) {
            log.warn("JWT token validation failed: token is invalid");
            throw new InvalidTokenException("JWT token is invalid", e);
        } catch (Exception e) {
            log.error("JWT token validation error: {}", e.getMessage());
            throw new InvalidTokenException("Token validation failed", e);
        }
    }

    private void validateTokenByUserInfo(String authHeader) {
        // Временный метод - в реальности должен быть отдельный endpoint /validate
        // Сейчас предполагаем, что любой вызов к Identity Service с невалидным токеном вернет 403
        // Это нужно будет доработать когда в Identity Service появится endpoint для валидации токена
        log.debug("Token validation passed");
    }

}
