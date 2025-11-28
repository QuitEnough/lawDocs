package com.yana.api.service;

import com.yana.api.exception.AuthenticationFailedException;
import com.yana.api.feign.IdentityServiceFeign;
import com.yana.lawdocs.api.identity.dto.AuthenticationRequest;
import com.yana.lawdocs.api.identity.dto.AuthenticationResponse;
import com.yana.lawdocs.api.identity.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final IdentityServiceFeign identityFeign;

    public AuthenticationResponse registerUser(RegisterRequest request) {
        try {
            log.info("Registering user in Identity Service: {}", request.getEmail());
            var response = identityFeign.register(request);
            log.info("User registered successfully in Identity Service, userId: {}", response.getUserId());
            return response;
        } catch (Exception e) {
            log.error("Identity Service registration failed for: {}", request.getEmail(), e);
            throw new AuthenticationFailedException("User registration failed in Identity Service");
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            log.info("Attempting authentication for user: {}", request.getEmail());
            var response = identityFeign.authenticate(request);
            log.info("Authentication successful for user: {}", request.getEmail());
            return response;
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getEmail(), e);
            throw new AuthenticationFailedException("Authentication failed : " + e.getMessage());
        }
    }

    public void deleteUserAccount(String authHeader) {
        try {
            log.info("Deleting user account");
            identityFeign.deleteAccount(authHeader);
            log.info("User account deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete user account", e);
            throw new AuthenticationFailedException("Account deletion failed");
        }
    }

}
