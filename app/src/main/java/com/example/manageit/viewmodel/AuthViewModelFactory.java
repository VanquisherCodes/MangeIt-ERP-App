package com.example.manageit.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.manageit.domain.auth.LoginUseCase;
import com.example.manageit.domain.auth.RegisterUseCase;

public class AuthViewModelFactory implements ViewModelProvider.Factory {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    public AuthViewModelFactory(LoginUseCase loginUseCase, RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return modelClass.cast(new AuthViewModel(loginUseCase, registerUseCase));
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
