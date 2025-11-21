package com.yana.identityservice.rest;

import com.yana.identityservice.api.client.AuthApi;
import com.yana.identityservice.api.dto.AuthenticationRequest;
import com.yana.identityservice.api.dto.AuthenticationResponse;
import com.yana.identityservice.api.dto.RegisterRequest;
import com.yana.identityservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController implements AuthApi {

    private final AuthenticationService service;

    @Override
    public ResponseEntity<AuthenticationResponse> register(RegisterRequest registerRequest) {
        log.info("Received RegisterRequest: {}", registerRequest);
        return ResponseEntity.ok(service.register(registerRequest));
    }

    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        log.info("Authentication request: {}", authenticationRequest);
        return ResponseEntity.ok(service.authenticate(authenticationRequest));
    }

}
