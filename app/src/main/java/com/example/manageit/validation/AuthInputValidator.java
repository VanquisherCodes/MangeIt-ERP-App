package com.example.manageit.validation;

import java.util.regex.Pattern;

public final class AuthInputValidator {

    private static final Pattern BASIC_EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private AuthInputValidator() {
    }

    public static AuthValidationResult validateLogin(String email, String password) {
        if (isBlank(email)) {
            return AuthValidationResult.invalid(AuthField.EMAIL, "Enter your email.");
        }
        if (!BASIC_EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return AuthValidationResult.invalid(AuthField.EMAIL, "Enter a valid email address.");
        }
        if (isBlank(password)) {
            return AuthValidationResult.invalid(AuthField.PASSWORD, "Enter your password.");
        }
        return AuthValidationResult.valid();
    }

    public static AuthValidationResult validateRegistration(
            String firstName,
            String lastName,
            String dateOfBirth,
            String email,
            String password
    ) {
        if (isBlank(firstName)) {
            return AuthValidationResult.invalid(AuthField.FIRST_NAME, "Enter a first name.");
        }
        if (isBlank(lastName)) {
            return AuthValidationResult.invalid(AuthField.LAST_NAME, "Enter a last name.");
        }
        if (isBlank(dateOfBirth)) {
            return AuthValidationResult.invalid(AuthField.DATE_OF_BIRTH, "Enter date of birth.");
        }
        if (isBlank(email)) {
            return AuthValidationResult.invalid(AuthField.EMAIL, "Enter an email address.");
        }
        if (!BASIC_EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return AuthValidationResult.invalid(AuthField.EMAIL, "Enter a valid email address.");
        }
        if (password == null || password.length() < 6) {
            return AuthValidationResult.invalid(AuthField.PASSWORD, "Use at least 6 characters.");
        }
        return AuthValidationResult.valid();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
