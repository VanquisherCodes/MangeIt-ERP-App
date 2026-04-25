package com.example.manageit.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.manageit.ManageItApplication;
import com.example.manageit.R;
import com.example.manageit.activities.MainActivity;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.User;
import com.example.manageit.utils.Resource;
import com.example.manageit.validation.AuthField;
import com.example.manageit.validation.AuthValidationResult;
import com.example.manageit.viewmodel.AuthViewModel;

/**
 * Login screen backed by the StudEV auth services.
 */
public class LoginFragment extends Fragment {

    private AuthViewModel viewModel;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText emailInput = view.findViewById(R.id.et_login_email);
        EditText passwordInput = view.findViewById(R.id.et_login_password);
        Button loginButton = view.findViewById(R.id.btn_login);
        Button goToRegisterButton = view.findViewById(R.id.btn_go_register);
        ManageItApplication application = (ManageItApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(
                this,
                application.getAppContainer().createAuthViewModelFactory()
        ).get(AuthViewModel.class);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            viewModel.login(email, password);
        });

        goToRegisterButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_auth, new RegisterFragment())
                .addToBackStack(null)
                .commit());

        viewModel.getValidationState().observe(getViewLifecycleOwner(), result ->
                showValidationError(result, emailInput, passwordInput));
        viewModel.getAuthState().observe(getViewLifecycleOwner(), state ->
                renderAuthState(state, emailInput, passwordInput, loginButton, goToRegisterButton));
    }

    private void showValidationError(
            AuthValidationResult result,
            EditText emailInput,
            EditText passwordInput
    ) {
        if (result == null || result.isValid()) {
            return;
        }

        if (result.getField() == AuthField.EMAIL) {
            emailInput.setError(result.getMessage());
            emailInput.requestFocus();
            return;
        }

        if (result.getField() == AuthField.PASSWORD) {
            passwordInput.setError(result.getMessage());
            passwordInput.requestFocus();
        }
    }

    private void renderAuthState(
            Resource<User> state,
            EditText emailInput,
            EditText passwordInput,
            Button loginButton,
            Button goToRegisterButton
    ) {
        if (state == null) {
            return;
        }

        if (state.status == Resource.Status.LOADING) {
            setLoadingState(emailInput, passwordInput, loginButton, goToRegisterButton, true, "Signing in...");
            return;
        }

        if (state.status == Resource.Status.ERROR) {
            setLoadingState(emailInput, passwordInput, loginButton, goToRegisterButton, false, "Login");
            passwordInput.setError(state.message);
            passwordInput.requestFocus();
            return;
        }

        if (state.status == Resource.Status.SUCCESS && state.data != null) {
            setLoadingState(emailInput, passwordInput, loginButton, goToRegisterButton, false, "Login");
            createSessionAndOpenMain(state.data);
            viewModel.resetState();
        }
    }

    private void createSessionAndOpenMain(User user) {
        if (!isAdded()) {
            return;
        }

        SessionManager sessionManager = new SessionManager(requireContext());
        sessionManager.createSession(user);

        Toast.makeText(requireContext(), "Welcome back, " + user.getFullName(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
    }

    private void setLoadingState(
            EditText emailInput,
            EditText passwordInput,
            Button loginButton,
            Button goToRegisterButton,
            boolean loading,
            String buttonText
    ) {
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
        loginButton.setEnabled(!loading);
        goToRegisterButton.setEnabled(!loading);
        loginButton.setText(buttonText);
    }
}
