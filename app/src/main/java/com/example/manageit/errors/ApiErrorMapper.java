package com.example.manageit.errors;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.ResponseBody;
import retrofit2.Response;

public final class ApiErrorMapper {

    private static final String NETWORK_ERROR = "Couldn't reach the server. Check your connection and try again.";

    private ApiErrorMapper() {
    }

    public static String networkError() {
        return NETWORK_ERROR;
    }

    public static String fromResponse(Response<?> response, String fallback) {
        if (response == null) {
            return fallback;
        }

        try {
            ResponseBody errorBody = response.errorBody();
            if (errorBody == null) {
                return fallback;
            }

            String rawError = errorBody.string();
            if (rawError == null || rawError.trim().isEmpty()) {
                return fallback;
            }

            return extractMessage(rawError.trim());
        } catch (IOException ignored) {
            return fallback;
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static String extractMessage(String rawError) {
        try {
            JsonObject object = JsonParser.parseString(rawError).getAsJsonObject();
            if (object.has("message") && !object.get("message").isJsonNull()) {
                String message = object.get("message").getAsString();
                if (message != null && !message.trim().isEmpty()) {
                    return message.trim();
                }
            }
        } catch (RuntimeException ignored) {
            // Some StudEV endpoints return plain text errors.
        }
        return rawError;
    }
}
