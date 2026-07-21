package com.maven.spring.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDto {

    private boolean success;

    private String message;

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long accessTokenExpiresIn;
}
