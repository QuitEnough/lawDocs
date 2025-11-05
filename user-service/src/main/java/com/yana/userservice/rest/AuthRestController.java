package com.yana.userservice.rest;

import com.yana.userservice.dto.AuthenticationRequest;
import com.yana.userservice.dto.JwtAuthenticationResponse;
import com.yana.userservice.dto.UserCreateRequest;
import com.yana.userservice.service.JwtService;
import com.yana.userservice.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@AllArgsConstructor
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService tokenService;
    private final UserServiceImpl userService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody @Valid UserCreateRequest request) {
        userService.addUser(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/auth")
    public JwtAuthenticationResponse authenticate(@RequestBody @Valid AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        return new JwtAuthenticationResponse(tokenService.generateToken(user));
    }

}
