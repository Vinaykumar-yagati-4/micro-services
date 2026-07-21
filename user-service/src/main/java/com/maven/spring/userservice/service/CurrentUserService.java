package com.maven.spring.userservice.service;


import com.maven.spring.userservice.dto.current.CurrentUserDto;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public CurrentUserDto getCurrentUser(
            String userId,
            String email,
            String role
    ) {

        Long convertedUserId = Long.parseLong(userId);

        return new CurrentUserDto(
                convertedUserId,
                email,
                role
        );
    }
}