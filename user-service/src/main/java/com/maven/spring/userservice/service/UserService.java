package com.maven.spring.userservice.service;

import org.springframework.cache.annotation.Cacheable;
import com.maven.spring.userservice.dto.request.UserProfileRequestDto;
import com.maven.spring.userservice.dto.response.ProfileResponseDto;
import com.maven.spring.userservice.entity.UserEntity;
import com.maven.spring.userservice.entity.Role;
import com.maven.spring.userservice.exception.UserNotFoundException;
import com.maven.spring.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;



    @Cacheable(
            value = "userProfiles",
            key = "#authUserId"
    )
    public ProfileResponseDto getProfile(Long authUserId) {

        System.out.println(
                "Fetching profile from MySQL for authUserId: "
                        + authUserId
        );

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

    public void createUserProfile(UserProfileRequestDto request) {

        if (userRepository.existsByAuthUserId(request.getAuthUserId())) {
            return;
        }

        UserEntity user = UserEntity.builder()
                .authUserId(request.getAuthUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .role(Role.valueOf(request.getRole()))
                .emailVerified(false)
                .accountEnabled(true)
                .build();

        userRepository.save(user);
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