package com.yana.api.config;

import com.yana.api.service.TokenValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenValidationInterceptor implements HandlerInterceptor {

    private final TokenValidationService tokenValidationService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         return false;
        }

        try {
            tokenValidationService.validateToken(authHeader);
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }
}
