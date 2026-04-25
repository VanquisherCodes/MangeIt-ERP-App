package com.example.manageit.validation;

public class AuthValidationResult {

    private static final AuthValidationResult VALID = new AuthValidationResult(true, AuthField.NONE, null);

    private final boolean valid;
    private final AuthField field;
    private final String message;

    private AuthValidationResult(boolean valid, AuthField field, String message) {
        this.valid = valid;
        this.field = field;
        this.message = message;
    }

    public static AuthValidationResult valid() {
        return VALID;
    }

    public static AuthValidationResult invalid(AuthField field, String message) {
        return new AuthValidationResult(false, field, message);
    }

    public boolean isValid() {
        return valid;
    }

    public AuthField getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
