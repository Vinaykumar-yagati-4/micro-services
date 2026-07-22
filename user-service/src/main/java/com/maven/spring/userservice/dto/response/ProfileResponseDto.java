package com.maven.spring.userservice.dto.response;

import com.maven.spring.userservice.entity.Role;
import com.netflix.discovery.provider.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto implements Serializable {

    private Long id;

    private Long authUserId;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private boolean emailVerified;

    private boolean accountEnabled;
}