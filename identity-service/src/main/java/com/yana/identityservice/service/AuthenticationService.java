package com.yana.identityservice.service;

import com.yana.identityservice.api.dto.AuthenticationRequest;
import com.yana.identityservice.api.dto.AuthenticationResponse;
import com.yana.identityservice.api.dto.RegisterRequest;
import com.yana.identityservice.entity.User;
import com.yana.identityservice.exception.UserNotFoundException;
import com.yana.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .role(request.getRole())
                .email(request.getEmail())
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        repository.save(user);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return getResponse(jwtToken, refreshToken, user.getId());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        UserDetails userDetails = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Long userId = ((User) userDetails).getId();
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return getResponse(jwtToken, refreshToken, userId);
    }

    private static AuthenticationResponse getResponse(String jwtToken, String refreshToken, Long userId) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwtToken);
        authenticationResponse.setRefreshToken(refreshToken);
        authenticationResponse.setUserId(userId);
        return authenticationResponse;
    }

    @Transactional
    public void deleteAccount(Long userId) {
        if (!repository.existsById(userId)) {
            return;
        }

        repository.deleteById(userId);
    }

}
