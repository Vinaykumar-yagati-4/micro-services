package com.maven.spring.userservice.service;

import com.maven.spring.userservice.dto.response.ProfileResponseDto;
import com.maven.spring.userservice.entity.UserEntity;
import com.maven.spring.userservice.exception.UserNotFoundException;
import com.maven.spring.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ProfileResponseDto getProfile(Long authUserId) {

        UserEntity user = userRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User profile not found for authentication user ID: "
                                        + authUserId
                        )
                );

        return convertToProfileResponse(user);
    }

    private ProfileResponseDto convertToProfileResponse(
            UserEntity user
    ) {

        return ProfileResponseDto.builder()
                .id(user.getId())
                .authUserId(user.getAuthUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .accountEnabled(user.isAccountEnabled())
                .build();
    }
}