package com.example.manageit.repository;

import com.example.manageit.models.User;
import com.example.manageit.network.ApiClient;
import com.example.manageit.utils.PasswordUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Authentication data operations backed by StudEV services.
 */
public class AuthRepository {

    private final ApiClient apiClient;

    public AuthRepository() {
        this.apiClient = ApiClient.getInstance();
    }

    public String getBaseUrl() {
        return apiClient.getBaseUrl();
    }

    public void login(String email, String password, RepositoryCallback<User> callback) {
        String normalizedEmail = email.trim();
        String hashedPassword = PasswordUtils.sha256(password);

        loginWithPassword(normalizedEmail, hashedPassword, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String message) {
                // Backward-compatible fallback for accounts stored before password hashing was added.
                loginWithPassword(normalizedEmail, password, callback);
            }
        });
    }

    public void register(
            String firstName,
            String lastName,
            String dateOfBirth,
            String email,
            String password,
            RepositoryCallback<User> callback
    ) {
        String normalizedEmail = email.trim();
        String hashedPassword = PasswordUtils.sha256(password);

        apiClient.getApiService()
                .register(
                        firstName.trim(),
                        lastName.trim(),
                        dateOfBirth.trim(),
                        normalizedEmail,
                        hashedPassword
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            callback.onError(extractErrorMessage(
                                    response,
                                    "Registration failed. Check whether the email already exists."
                            ));
                            return;
                        }

                        loginWithPassword(normalizedEmail, hashedPassword, callback);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server. Check your connection and try again.");
                    }
                });
    }

    private void loginWithPassword(String email, String passwordValue, RepositoryCallback<User> callback) {
        apiClient.getApiService()
                .login(email, passwordValue)
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        if (!response.isSuccessful()) {
                            callback.onError(extractErrorMessage(response, "Login failed. Please try again."));
                            return;
                        }

                        List<User> users = response.body();
                        if (users == null || users.isEmpty()) {
                            callback.onError("Incorrect email or password.");
                            return;
                        }

                        callback.onSuccess(users.get(0));
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server. Check your connection and try again.");
                    }
                });
    }

    private String extractErrorMessage(Response<?> response, String fallback) {
        try {
            ResponseBody errorBody = response.errorBody();
            if (errorBody == null) {
                return fallback;
            }

            String rawError = errorBody.string();
            if (rawError == null || rawError.trim().isEmpty()) {
                return fallback;
            }

            return rawError.trim();
        } catch (IOException ignored) {
            return fallback;
        }
    }
}
