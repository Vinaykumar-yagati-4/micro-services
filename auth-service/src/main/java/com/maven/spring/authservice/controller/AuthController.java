package com.maven.spring.authservice.controller;

import com.maven.spring.authservice.dto.request.LoginRequestDto;
import com.maven.spring.authservice.dto.request.RefreshTokenRequestDto;
import com.maven.spring.authservice.dto.request.SignupRequestDto;
import com.maven.spring.authservice.dto.response.LoginResponseDto;
import com.maven.spring.authservice.dto.response.RefreshTokenResponseDto;
import com.maven.spring.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ---------------- SIGNUP ----------------

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @RequestBody SignupRequestDto signupRequestDto
    ) {

        String response =
                authService.signup(signupRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ---------------- LOGIN ----------------

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto
    ) {

        LoginResponseDto response =
                authService.login(loginRequestDto);

        return ResponseEntity.ok(response);
    }

    // ---------------- REFRESH TOKEN ----------------

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(
            @RequestBody RefreshTokenRequestDto requestDto
    ) {

        RefreshTokenResponseDto response =
                authService.refreshToken(requestDto);

        return ResponseEntity.ok(response);
    }
}