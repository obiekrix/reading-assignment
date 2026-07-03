package com.takehometask.readingassignment.user;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/students")
    public List<AppUser> getStudents() {
        return userRepository.findByRole(UserRole.STUDENT);
    }
}