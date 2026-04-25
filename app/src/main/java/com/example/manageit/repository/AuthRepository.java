package com.example.manageit.repository;

import com.example.manageit.errors.ApiErrorMapper;
import com.example.manageit.models.User;
import com.example.manageit.network.ApiClient;
import com.example.manageit.repository.contracts.AuthRepositoryContract;
import com.example.manageit.utils.PasswordUtils;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Authentication data operations backed by StudEV services.
 */
public class AuthRepository implements AuthRepositoryContract {

    private final ApiClient apiClient;

    public AuthRepository() {
        this(ApiClient.getInstance());
    }

    public AuthRepository(ApiClient apiClient) {
        this.apiClient = apiClient;
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
                            callback.onError(ApiErrorMapper.fromResponse(
                                    response,
                                    "Registration failed. Check whether the email already exists."
                            ));
                            return;
                        }

                        loginWithPassword(normalizedEmail, hashedPassword, callback);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError(ApiErrorMapper.networkError());
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
                            callback.onError(ApiErrorMapper.fromResponse(response, "Login failed. Please try again."));
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
                        callback.onError(ApiErrorMapper.networkError());
                    }
                });
    }
}
