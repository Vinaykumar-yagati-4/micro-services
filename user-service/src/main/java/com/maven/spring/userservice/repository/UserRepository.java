package com.maven.spring.userservice.repository;

import com.maven.spring.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByAuthUserId(Long authUserId);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByAuthUserId(Long authUserId);

    boolean existsByEmail(String email);
}