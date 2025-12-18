package com.yana.api.feign;

import com.yana.lawdocs.api.gateway.dto.TelegramAuthRequest;
import com.yana.lawdocs.api.gateway.dto.TelegramAuthResponse;
import com.yana.lawdocs.api.identity.dto.AuthenticationRequest;
import com.yana.lawdocs.api.identity.dto.AuthenticationResponse;
import com.yana.lawdocs.api.identity.dto.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-service", url = "${identity-service.url}/api/v1/auth")
public interface IdentityServiceFeign {

    @PostMapping("/register")
    AuthenticationResponse register(@RequestBody RegisterRequest request);

    @PostMapping("/authenticate")
    AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request);

    @DeleteMapping("/delete-account")
    ResponseEntity<Void> deleteAccount(@RequestHeader("Authorization") String authHeader);

    // TODO: Добавить когда будет реализовано в Identity Service
    // @PostMapping("/validate-token")
    // ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader);

    @PostMapping("/telegram")
    TelegramAuthResponse authenticateTelegram(@RequestBody TelegramAuthRequest request);

}
