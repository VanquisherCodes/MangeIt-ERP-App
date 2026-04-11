package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Membership role within a student group.
 */
public enum Role {
    @SerializedName("admin")
    ADMIN,
    @SerializedName("user")
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
