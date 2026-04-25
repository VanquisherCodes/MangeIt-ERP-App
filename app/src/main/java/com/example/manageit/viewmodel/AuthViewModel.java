package com.example.manageit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.manageit.domain.auth.LoginUseCase;
import com.example.manageit.domain.auth.RegisterUseCase;
import com.example.manageit.models.User;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.utils.Resource;
import com.example.manageit.validation.AuthInputValidator;
import com.example.manageit.validation.AuthValidationResult;

public class AuthViewModel extends ViewModel {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final MutableLiveData<Resource<User>> authState = new MutableLiveData<>(Resource.empty());
    private final MutableLiveData<AuthValidationResult> validationState =
            new MutableLiveData<>(AuthValidationResult.valid());

    public AuthViewModel(LoginUseCase loginUseCase, RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    public LiveData<Resource<User>> getAuthState() {
        return authState;
    }

    public LiveData<AuthValidationResult> getValidationState() {
        return validationState;
    }

    public void login(String email, String password) {
        AuthValidationResult validationResult = AuthInputValidator.validateLogin(email, password);
        validationState.setValue(validationResult);
        if (!validationResult.isValid()) {
            return;
        }

        authState.setValue(Resource.loading());
        loginUseCase.execute(email, password, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                authState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String message) {
                authState.setValue(Resource.error(message));
            }
        });
    }

    public void register(String firstName, String lastName, String dateOfBirth, String email, String password) {
        AuthValidationResult validationResult = AuthInputValidator.validateRegistration(
                firstName,
                lastName,
                dateOfBirth,
                email,
                password
        );
        validationState.setValue(validationResult);
        if (!validationResult.isValid()) {
            return;
        }

        authState.setValue(Resource.loading());
        registerUseCase.execute(firstName, lastName, dateOfBirth, email, password, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                authState.setValue(Resource.success(result));
            }

            @Override
            public void onError(String message) {
                authState.setValue(Resource.error(message));
            }
        });
    }

    public void resetState() {
        authState.setValue(Resource.empty());
        validationState.setValue(AuthValidationResult.valid());
    }
}
