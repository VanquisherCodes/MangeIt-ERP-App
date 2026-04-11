package com.example.manageit.repository;

import com.example.manageit.models.User;
import com.example.manageit.network.ApiClient;

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
        apiClient.getApiService()
                .login(email.trim(), password)
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

    public void register(
            String firstName,
            String lastName,
            String dateOfBirth,
            String email,
            String password,
            RepositoryCallback<User> callback
    ) {
        apiClient.getApiService()
                .register(
                        firstName.trim(),
                        lastName.trim(),
                        dateOfBirth.trim(),
                        email.trim(),
                        password
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

                        login(email, password, callback);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
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
