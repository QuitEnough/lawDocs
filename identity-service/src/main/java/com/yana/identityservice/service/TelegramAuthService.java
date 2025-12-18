package com.yana.identityservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yana.identityservice.api.dto.Role;
import com.yana.identityservice.api.dto.TelegramAuthRequest;
import com.yana.identityservice.api.dto.TelegramAuthResponse;
import com.yana.identityservice.entity.User;
import com.yana.identityservice.exception.HashNotFoundException;
import com.yana.identityservice.exception.InvalidTelegramHash;
import com.yana.identityservice.exception.TelegramAuthenticationFailedException;
import com.yana.identityservice.exception.TelegramDataExpiredException;
import com.yana.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAuthService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public TelegramAuthResponse authenticate(TelegramAuthRequest request) {
        // 1. Валидируем Telegram данные
        Map<String, String> dataMap = validateTelegramInitData(request.getInitData());

        // 2. Извлекаем данные пользователя
        String telegramId = extractTelegramId(dataMap);
        Map<String, Object> userData = extractUserData(dataMap);

        var user = findOrCreateUser(telegramId, userData);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        TelegramAuthResponse response = new TelegramAuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setUserId(user.getId());
        response.setTelegramId(telegramId);
        response.setExpiresIn(3600);

        return response;
    }

    private Map<String, String> validateTelegramInitData(String initData) {
        try {
            var pairs = initData.split("&");
            Map<String, String> dataMap = new HashMap<>();
            String receivedHash = null;

            for (String pair : pairs) {
                var keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    var key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    var value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

                    if ("hash".equals(key)) {
                        receivedHash = value;
                    } else {
                        dataMap.put(key, value);
                    }
                }
            }

            if (receivedHash == null) {
                throw new HashNotFoundException("Hash not found in initData");
            }

            List<String> dataCheckList = dataMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .toList();

            var dataCheckString = String.join("\n", dataCheckList);

            var secretKey = "WebAppData";
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    MessageDigest.getInstance("SHA-256").digest(botToken.getBytes()),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);

            byte[] hashBytes = mac.doFinal(dataCheckString.getBytes());
            var hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            var calculatedHash = hexString.toString();

            if (!calculatedHash.equals(receivedHash)) {
                throw new InvalidTelegramHash("Invalid Telegram hash");
            }

            String authDateStr = dataMap.get("auth_date");
            if (authDateStr != null) {
                long authDate = Long.parseLong(authDateStr);
                long currentTime = System.currentTimeMillis() / 1000;
                if (currentTime - authDate > 86400) { // 24 часа
                    throw new TelegramDataExpiredException("Telegram data expired");
                }
            }
            return dataMap;

        } catch (Exception e) {
            log.error("Telegram initData validation failed", e);
            throw new TelegramAuthenticationFailedException("Telegram authentication failed: " + e.getMessage());
        }
    }

    private String extractTelegramId(Map<String, String> dataMap) {
        String userJson = dataMap.get("user");
        if (userJson != null) {
            try {
                Map<String, Object> userMap = objectMapper.readValue(userJson, Map.class);
                Object id = userMap.get("id");
                if (id != null) {
                    return id.toString();
                }
            } catch (Exception e) {
                log.error("Failed to parse user data", e);
            }
        }
        throw new RuntimeException("Telegram user ID not found");
    }

    private Map<String, Object> extractUserData(Map<String, String> dataMap) {
        var userDataJson = dataMap.get("user");
        if (userDataJson == null) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(userDataJson, Map.class);
        } catch (Exception e) {
            log.error("Failed to parse Telegram user data", e);
            return new HashMap<>();
        }
    }

    private User findOrCreateUser(String telegramId, Map<String, Object> userData) {
        Optional<User> existingUser = userRepository.findByTelegramId(telegramId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        var newUser = User.builder()
                .telegramId(telegramId)
                .firstName((String) userData.getOrDefault("first_name", ""))
                .lastName((String) userData.getOrDefault("last_name", ""))
                .email((String) userData.getOrDefault("username", ""))
                .password("") // пустой пароль для Telegram пользователей
                .role(Role.USER)
                .build();

        return userRepository.save(newUser);
    }

}
