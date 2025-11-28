package com.yana.api.service;

import com.yana.api.exception.RegistrationFailedException;
import com.yana.lawdocs.api.identity.dto.AuthenticationResponse;
import com.yana.lawdocs.api.identity.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final AuthenticationService authenticationService;
    private final FileStorageService fileStorageService;
    private final CompensationService compensationService;

    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Starting registration process for user: {}", request.getEmail());

        AuthenticationResponse authResponse;
        try {
            authResponse = authenticationService.registerUser(request);
            log.info("User registered successfully in Identity Service, userId: {}", authResponse.getUserId());
        } catch (Exception e) {
            log.error("Failed to register user in Identity Service: {}", request.getEmail(), e);
            throw new RegistrationFailedException("User registration failed in Identity Service");
        }

        var authHeader = "Bearer " + authResponse.getToken();
        var userId = authResponse.getUserId();

        try {
            log.info("Creating root directory for user: {}", userId);
            fileStorageService.createDirectory(authHeader, "root directory", null);
            log.info("Root directory created successfully for user: {}", userId);
            return authResponse;
        } catch (Exception e) {
            log.error("Failed to create root directory for user: {}. Starting compensation... ", userId, e);
            compensationService.compensateUserRegistration(authHeader, userId);
            throw new RegistrationFailedException("Registration failed at file storage step");
        }
    }

}
