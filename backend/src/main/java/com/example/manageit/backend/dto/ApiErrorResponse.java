package com.example.manageit.backend.dto;

import java.time.Instant;

public class ApiErrorResponse {

    private final String message;
    private final String generatedAt;

    public ApiErrorResponse(String message) {
        this.message = message;
        this.generatedAt = Instant.now().toString();
    }

    public String getMessage() {
        return message;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }
}
