package com.example.manageit.domain.auth;

import com.example.manageit.models.User;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.repository.contracts.AuthRepositoryContract;

public class RegisterUseCase {

    private final AuthRepositoryContract authRepository;

    public RegisterUseCase(AuthRepositoryContract authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(
            String firstName,
            String lastName,
            String dateOfBirth,
            String email,
            String password,
            RepositoryCallback<User> callback
    ) {
        authRepository.register(firstName, lastName, dateOfBirth, email, password, callback);
    }
}
