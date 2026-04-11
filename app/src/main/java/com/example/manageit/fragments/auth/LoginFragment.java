package com.example.manageit.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.manageit.R;
import com.example.manageit.activities.MainActivity;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.User;
import com.example.manageit.repository.AuthRepository;
import com.example.manageit.repository.RepositoryCallback;

/**
 * Login screen backed by the StudEV auth services.
 */
public class LoginFragment extends Fragment {

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
        AuthRepository authRepository = new AuthRepository();

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Enter your email.");
                emailInput.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Enter a valid email address.");
                emailInput.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Enter your password.");
                passwordInput.requestFocus();
                return;
            }

            setLoadingState(emailInput, passwordInput, loginButton, goToRegisterButton, true, "Signing in...");
            authRepository.login(email, password, new RepositoryCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    if (!isAdded()) {
                        return;
                    }

                    SessionManager sessionManager = new SessionManager(requireContext());
                    sessionManager.createSession(user);

                    Toast.makeText(requireContext(), "Welcome back, " + user.getFullName(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireContext(), MainActivity.class));
                    requireActivity().finish();
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) {
                        return;
                    }

                    setLoadingState(emailInput, passwordInput, loginButton, goToRegisterButton, false, "Login");
                    passwordInput.setError(message);
                    passwordInput.requestFocus();
                }
            });
        });

        goToRegisterButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_auth, new RegisterFragment())
                .addToBackStack(null)
                .commit());
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
