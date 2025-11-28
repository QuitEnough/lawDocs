package com.yana.api.service;

import com.yana.api.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompensationService {

    private final AuthenticationService authenticationService;

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void compensateUserRegistration(String authHeader, Long userId) {
        try {
            log.warn("Executing compensation: deleting user account {}", userId);
            authenticationService.deleteUserAccount(authHeader);
            log.info("Compensation successful: user account {} deleted", userId);
        } catch (Exception rollbackEx) {
            log.error("CRITICAL: Failed to rollback user registration for id {}", userId, rollbackEx);
            throw new RegistrationFailedException("Compensation failed for user: " + userId);
        }
    }

    @Recover
    public void recoverCompensation(Exception e, String authHeader, Long userId) {
        log.error("All compensation attempts failed for user: {}. Manual intervention required!", userId, e);
        // Можно отправить уведомление, записать в спец. лог и т.д.
    }

}
