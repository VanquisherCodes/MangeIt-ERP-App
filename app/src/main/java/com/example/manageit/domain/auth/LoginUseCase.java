package com.example.manageit.domain.auth;

import com.example.manageit.models.User;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.repository.contracts.AuthRepositoryContract;

public class LoginUseCase {

    private final AuthRepositoryContract authRepository;

    public LoginUseCase(AuthRepositoryContract authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String password, RepositoryCallback<User> callback) {
        authRepository.login(email, password, callback);
    }
}
