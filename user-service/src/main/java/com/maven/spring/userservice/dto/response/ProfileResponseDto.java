package com.maven.spring.userservice.dto.response;

import com.maven.spring.userservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto {

    private Long id;

    private Long authUserId;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private boolean emailVerified;

    private boolean accountEnabled;
}