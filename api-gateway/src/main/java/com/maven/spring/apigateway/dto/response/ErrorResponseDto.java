package com.maven.spring.apigateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
    private boolean success;

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

}
