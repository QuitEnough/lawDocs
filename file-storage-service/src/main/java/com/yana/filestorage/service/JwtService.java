package com.yana.filestorage.service;

import com.yana.filestorage.dto.UserInfoBACKUP;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;

@Service
public class JwtService {

    private final SecretKey signingKey;

    public JwtService(@Value("${token.signing.key}") String secretKey) {
        var keyBytes = Base64.getDecoder().decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public UserInfoBACKUP extractUserInfo(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = claims.get("id", Long.class);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        return new UserInfoBACKUP(userId, email, role);
    }

}
