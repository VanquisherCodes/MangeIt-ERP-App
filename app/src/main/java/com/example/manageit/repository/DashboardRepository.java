package com.example.manageit.repository;

import com.example.manageit.models.Role;

/**
 * Repository for dashboard-related data aggregation.
 */
public class DashboardRepository {

    public String getWelcomeLabel(Role role) {
        return role == Role.ADMIN ? "Admin ERP Dashboard" : "Student ERP Dashboard";
    }
}
