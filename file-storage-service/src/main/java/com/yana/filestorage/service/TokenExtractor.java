package com.yana.filestorage.service;

import com.yana.filestorage.api.dto.UserInfo;
import com.yana.filestorage.exception.UserNotAuthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenExtractor {

    private final JwtService jwtService;

    public UserInfo extractFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserNotAuthenticatedException("Missing or invalid Authorization header");
        }
        var token = authHeader.substring(7);
        return jwtService.extractUserInfo(token);
    }

}
