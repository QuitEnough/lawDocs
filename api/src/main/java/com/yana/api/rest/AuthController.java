package com.yana.api.rest;

import com.yana.api.service.AuthenticationService;
import com.yana.api.service.RegistrationService;
import com.yana.lawdocs.api.identity.dto.AuthenticationRequest;
import com.yana.lawdocs.api.identity.dto.AuthenticationResponse;
import com.yana.lawdocs.api.identity.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для регистрации и авторизации пользователей")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Создает нового пользователя и root директорию")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        var response = registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Авторизация пользователя", description = "Аутентификация пользователя и выдача JWT токена")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

}
