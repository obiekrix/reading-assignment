package com.takehometask.readingassignment.user;

import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        AppUser user = userRepository.findByEmailAndRole(loginRequest.email(), UserRole.valueOf(loginRequest.role()));
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String expectedPassword = user.getName().replace(" ", "#");
        if (!expectedPassword.equals(loginRequest.password())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        return ResponseEntity.ok(user);
    }

    public record LoginRequest(String email, String password, String role) {
    }
}