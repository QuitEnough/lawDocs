package com.yana.api.service;

import com.yana.api.exception.RegistrationFailedException;
import com.yana.api.feign.FileStorageServiceFeign;
import com.yana.api.feign.IdentityServiceFeign;
import com.yana.lawdocs.api.identity.dto.AuthenticationResponse;
import com.yana.lawdocs.api.identity.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final IdentityServiceFeign identityFeign;
    private final FileStorageServiceFeign fileStorageFeign;

    public AuthenticationResponse register(RegisterRequest request) {
        var authResponse = identityFeign.register(request);
        var authHeader = "Bearer " + authResponse.getToken();
        var userId = authResponse.getUserId();

        try {
            fileStorageFeign.createDirectory(authHeader, "root directory", null);
            log.info("User {} registered and root directory created", userId);
            return authResponse;
        } catch (Exception e) {
            log.warn("File storage failed for user {}. Compensating...", userId, e);
            try {
                identityFeign.deleteAccount(authHeader);
                log.info("Compensation succeeded: account {} deleted", userId);
            } catch (Exception rollbackEx) {
                log.error("CRITICAL: failed to rollback user registration for id {}", userId, rollbackEx);
            }
            throw new RegistrationFailedException("Registration failed at file storage step");
        }
    }

}
