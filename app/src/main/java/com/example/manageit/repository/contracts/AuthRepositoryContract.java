package com.example.manageit.repository.contracts;

import com.example.manageit.models.User;
import com.example.manageit.repository.RepositoryCallback;

public interface AuthRepositoryContract {
    String getBaseUrl();

    void login(String email, String password, RepositoryCallback<User> callback);

    void register(
            String firstName,
            String lastName,
            String dateOfBirth,
            String email,
            String password,
            RepositoryCallback<User> callback
    );
}
