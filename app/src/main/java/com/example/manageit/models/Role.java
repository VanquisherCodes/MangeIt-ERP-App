package com.example.manageit.models;

/**
 * App roles used for access control and dashboard routing.
 */
public enum Role {
    ADMIN,
    USER;

    public static Role from(String value) {
        if (value == null) {
            return USER;
        }
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return USER;
        }
    }
}
