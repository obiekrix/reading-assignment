package com.takehometask.readingassignment.user;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String userId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    protected AppUser() {
    }

    public AppUser(String name, String email, String userId, UserRole role) {
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }
}