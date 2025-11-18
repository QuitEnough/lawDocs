package com.yana.filestorage.service;

import com.yana.filestorage.dto.UserInfoBACKUP;
import com.yana.filestorage.exception.UserNotAuthenticatedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenExtractor {

    private final JwtService jwtService;

    public UserInfoBACKUP extractFromRequest(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserNotAuthenticatedException("Missing or invalid Authorization header");
        }
        var token = authHeader.substring(7);
        return jwtService.extractUserInfo(token);
    }

}
