package com.takehometask.readingassignment.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findByRole(UserRole role);

    AppUser findByEmailAndRole(String email, UserRole role);

    Optional<AppUser> findByUserId(String userId);
}