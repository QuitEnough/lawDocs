package com.yana.filestorage.service;

import com.yana.filestorage.entity.UserDetailsImpl;
import com.yana.filestorage.exception.UserNotAuthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@Component("UserAccessor")
@RequiredArgsConstructor
public class CustomSecurityExpression {

    private final RestTemplate restTemplate;
    private final FileService fileService;
    private final DirectoryService directoryService;
    @Value("${user.service.url}")
    private String userServiceUrl;

    public boolean canUserAccessResource(final String resourceType, final Long resourceId) {
        UserDetailsImpl user = getPrincipal();
        Long userId = user.getId();

        if (hasAdminRole()) {
            return true;
        }

        if (!isUserExists(userId)) {
            return false;
        }

        boolean hasAccess = switch (resourceType.toLowerCase()) {
            case "file" -> fileService.isFileOwner(resourceId, userId);
            case "dir" -> directoryService.isDirectoryOwner(resourceId, userId);
            case "user" -> userId.equals(resourceId);
            default -> false;
        };

        return hasAccess;
    }

    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ADMIN"));
    }

    private boolean isUserExists(Long userId) {
        String url = userServiceUrl + "/users/" + userId + "/exists";

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Boolean.class);

        return Boolean.TRUE.equals(response.getBody());
    }

    private UserDetailsImpl getPrincipal() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        return (UserDetailsImpl) authentication.getPrincipal();
    }

}
