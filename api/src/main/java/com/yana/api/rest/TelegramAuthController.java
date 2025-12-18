package com.yana.api.rest;

import com.yana.api.feign.IdentityServiceFeign;
import com.yana.lawdocs.api.gateway.dto.TelegramAuthRequest;
import com.yana.lawdocs.api.gateway.dto.TelegramAuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TelegramAuthController {

    private final IdentityServiceFeign identityServiceFeign;

    @PostMapping("/telegram")
    @Operation(summary = "Аутентификация через Telegram WebApp")
    public ResponseEntity<TelegramAuthResponse> authenticateTelegram(
            @RequestBody TelegramAuthRequest request) {

        log.info("Forwarding Telegram authentication request to Identity Service");

        TelegramAuthResponse response = identityServiceFeign.authenticateTelegram(request);

        log.info("Telegram authentication processed, userId: {}", response.getUserId());
        return ResponseEntity.ok(response);
    }

}
