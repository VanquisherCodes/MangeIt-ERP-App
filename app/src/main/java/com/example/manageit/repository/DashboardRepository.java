package com.example.manageit.repository;

/**
 * Repository for dashboard-related data aggregation.
 */
public class DashboardRepository {

    public String getWelcomeLabel(String groupName) {
        return groupName + " Dashboard";
    }
}
