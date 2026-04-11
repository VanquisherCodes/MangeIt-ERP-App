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
 * Registration screen backed by the StudEV auth services.
 */
public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        super(R.layout.fragment_register);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText firstNameInput = view.findViewById(R.id.et_register_name);
        EditText lastNameInput = view.findViewById(R.id.et_register_last_name);
        EditText dateOfBirthInput = view.findViewById(R.id.et_register_dob);
        EditText emailInput = view.findViewById(R.id.et_register_email);
        EditText passwordInput = view.findViewById(R.id.et_register_password);
        Button registerButton = view.findViewById(R.id.btn_register);
        Button goToLoginButton = view.findViewById(R.id.btn_go_login);
        AuthRepository authRepository = new AuthRepository();

        registerButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String dateOfBirth = dateOfBirthInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (TextUtils.isEmpty(firstName)) {
                firstNameInput.setError("Enter a first name.");
                firstNameInput.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(lastName)) {
                lastNameInput.setError("Enter a last name.");
                lastNameInput.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(dateOfBirth)) {
                dateOfBirthInput.setError("Enter date of birth.");
                dateOfBirthInput.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Enter an email address.");
                emailInput.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Enter a valid email address.");
                emailInput.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password) || password.length() < 6) {
                passwordInput.setError("Use at least 6 characters.");
                passwordInput.requestFocus();
                return;
            }

            setLoadingState(
                    firstNameInput,
                    lastNameInput,
                    dateOfBirthInput,
                    emailInput,
                    passwordInput,
                    registerButton,
                    goToLoginButton,
                    true,
                    "Creating account..."
            );
            authRepository.register(firstName, lastName, dateOfBirth, email, password, new RepositoryCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    if (!isAdded()) {
                        return;
                    }

                    SessionManager sessionManager = new SessionManager(requireContext());
                    sessionManager.createSession(user);

                    Toast.makeText(requireContext(), "Account created for " + user.getFullName(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireContext(), MainActivity.class));
                    requireActivity().finish();
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) {
                        return;
                    }

                    setLoadingState(
                            firstNameInput,
                            lastNameInput,
                            dateOfBirthInput,
                            emailInput,
                            passwordInput,
                            registerButton,
                            goToLoginButton,
                            false,
                            "Register"
                    );
                    emailInput.setError(message);
                    emailInput.requestFocus();
                }
            });
        });

        goToLoginButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .popBackStack());
    }

    private void setLoadingState(
            EditText firstNameInput,
            EditText lastNameInput,
            EditText dateOfBirthInput,
            EditText emailInput,
            EditText passwordInput,
            Button registerButton,
            Button goToLoginButton,
            boolean loading,
            String buttonText
    ) {
        firstNameInput.setEnabled(!loading);
        lastNameInput.setEnabled(!loading);
        dateOfBirthInput.setEnabled(!loading);
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
        registerButton.setEnabled(!loading);
        goToLoginButton.setEnabled(!loading);
        registerButton.setText(buttonText);
    }
}
