package com.johanchereau.critiqbackend.util;

import com.johanchereau.critiqbackend.user.User;
import com.johanchereau.critiqbackend.user.UserRole;

import java.time.LocalDate;
import java.util.List;

public class TestUserFactory {

    private TestUserFactory() {
        // Private constructor => prevent instantiation
    }

    public static User createUser(String username, String email, List<UserRole> roles) {
        return User.builder()
                .firstname("John")
                .lastname("Doe")
                .username(username)
                .email(email)
                .password("testPassword")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .termsAccepted(true)
                .enabled(true)
                .userRoles(roles)
                .build();
    }

    public static User createDefaultUser() {
        return createUser("johndoe", "john.doe@example.com", List.of());
    }

    public static User createAdminUser() {
        UserRole adminRole = UserRole.builder()
                .name("ADMIN")
                .build();
        return createUser("admin", "admin@example.com", List.of(adminRole));
    }
}