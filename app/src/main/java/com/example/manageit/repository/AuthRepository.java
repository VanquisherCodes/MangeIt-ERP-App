package com.example.manageit.repository;

import com.example.manageit.network.ApiClient;

/**
 * Authentication data operations (login/register/session refresh).
 */
public class AuthRepository {

    private final ApiClient apiClient;

    public AuthRepository() {
        this.apiClient = ApiClient.getInstance();
    }

    public String getBaseUrl() {
        return apiClient.getBaseUrl();
    }
}
