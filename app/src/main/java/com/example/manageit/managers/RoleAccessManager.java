package com.example.manageit.managers;

import com.example.manageit.models.Role;

/**
 * Central role-based feature flags.
 */
public class RoleAccessManager {

    public boolean canManageUsers(Role role) {
        return role == Role.ADMIN;
    }

    public boolean canCreateAnnouncements(Role role) {
        return role == Role.ADMIN;
    }

    public boolean canSubmitRequests(Role role) {
        return role == Role.USER || role == Role.ADMIN;
    }
}
