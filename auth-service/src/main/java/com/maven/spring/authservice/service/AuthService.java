package com.maven.spring.authservice.service;

import com.maven.spring.authservice.dto.request.LoginRequestDto;
import com.maven.spring.authservice.dto.response.LoginResponseDto;
import com.maven.spring.authservice.dto.request.RefreshTokenRequestDto;
import com.maven.spring.authservice.dto.response.RefreshTokenResponseDto;
import com.maven.spring.authservice.dto.request.SignupRequestDto;
import com.maven.spring.authservice.entity.Role;
import com.maven.spring.authservice.entity.UserEntity;
import com.maven.spring.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    // ---------------- SIGNUP ----------------

    public String signup(SignupRequestDto signupRequestDto) {

        String email = signupRequestDto.getEmail()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        String encryptedPassword =
                passwordEncoder.encode(
                        signupRequestDto.getPassword()
                );

        UserEntity user = UserEntity.builder()
                .firstName(signupRequestDto.getFirstName().trim())
                .lastName(signupRequestDto.getLastName().trim())
                .email(email)
                .password(encryptedPassword)
                .role(Role.ROLE_USER)
                .emailVerified(false)
                .accountEnabled(true)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    // ---------------- LOGIN ----------------

    public LoginResponseDto login(
            LoginRequestDto loginRequestDto
    ) {

        String email = loginRequestDto.getEmail()
                .trim()
                .toLowerCase();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        boolean passwordMatches =
                passwordEncoder.matches(
                        loginRequestDto.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        if (!user.isAccountEnabled()) {
            throw new IllegalArgumentException(
                    "Your account is disabled"
            );
        }

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);

        return LoginResponseDto.builder()
                .success(true)
                .message("Login successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(
                        jwtService.getAccessTokenExpiration()
                )
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // ---------------- REFRESH TOKEN ----------------

    public RefreshTokenResponseDto refreshToken(
            RefreshTokenRequestDto requestDto
    ) {

        String refreshToken =
                requestDto.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Refresh token is required"
            );
        }

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException(
                    "Invalid refresh token"
            );
        }

        String email =
                jwtService.extractEmail(refreshToken);

        UserEntity user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        String newAccessToken =
                jwtService.generateAccessToken(user);

        String newRefreshToken =
                jwtService.generateRefreshToken(user);

        return RefreshTokenResponseDto.builder()
                .success(true)
                .message("Token refreshed successfully")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(
                        jwtService.getAccessTokenExpiration()
                )
                .build();
    }

}