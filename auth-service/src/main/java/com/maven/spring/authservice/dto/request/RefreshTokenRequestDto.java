package com.maven.spring.authservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDto {

    private boolean success;

    private String message;

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long accessTokenExpiresIn;
}