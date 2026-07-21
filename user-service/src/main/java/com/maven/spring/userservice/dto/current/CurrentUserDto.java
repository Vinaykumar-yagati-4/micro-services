package com.maven.spring.userservice.dto.current;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDto {

    private Long userId;

    private String email;

    private String role;
}