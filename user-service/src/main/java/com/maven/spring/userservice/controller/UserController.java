package com.maven.spring.userservice.controller;

import com.maven.spring.userservice.dto.request.UserProfileRequestDto;
import com.maven.spring.userservice.dto.response.ProfileResponseDto;
import com.maven.spring.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/hello")
    public String hello() {

        return "Hello World!";
    }

    @PostMapping("/internal")
    public ResponseEntity<String> createUserProfile(
            @RequestBody UserProfileRequestDto request
    ) {

        userService.createUserProfile(request);

        return ResponseEntity.ok("Profile Created");
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(
            @RequestHeader("X-User-Id") String userId
    ) {

        Long authUserId = Long.parseLong(userId);

        ProfileResponseDto profile =
                userService.getProfile(authUserId);

        return ResponseEntity.ok(profile);
    }
}