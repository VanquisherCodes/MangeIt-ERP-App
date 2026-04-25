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
 * Registration screen backed by the StudEV auth services.
 */
public class RegisterFragment extends Fragment {

    private AuthViewModel viewModel;

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
        ManageItApplication application = (ManageItApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(
                this,
                application.getAppContainer().createAuthViewModelFactory()
        ).get(AuthViewModel.class);

        registerButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String dateOfBirth = dateOfBirthInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            viewModel.register(firstName, lastName, dateOfBirth, email, password);
        });

        goToLoginButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .popBackStack());

        viewModel.getValidationState().observe(getViewLifecycleOwner(), result -> showValidationError(
                result,
                firstNameInput,
                lastNameInput,
                dateOfBirthInput,
                emailInput,
                passwordInput
        ));
        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> renderAuthState(
                state,
                firstNameInput,
                lastNameInput,
                dateOfBirthInput,
                emailInput,
                passwordInput,
                registerButton,
                goToLoginButton
        ));
    }

    private void showValidationError(
            AuthValidationResult result,
            EditText firstNameInput,
            EditText lastNameInput,
            EditText dateOfBirthInput,
            EditText emailInput,
            EditText passwordInput
    ) {
        if (result == null || result.isValid()) {
            return;
        }

        EditText target = resolveField(result.getField(), firstNameInput, lastNameInput, dateOfBirthInput, emailInput, passwordInput);
        if (target != null) {
            target.setError(result.getMessage());
            target.requestFocus();
        }
    }

    private EditText resolveField(
            AuthField field,
            EditText firstNameInput,
            EditText lastNameInput,
            EditText dateOfBirthInput,
            EditText emailInput,
            EditText passwordInput
    ) {
        if (field == AuthField.FIRST_NAME) {
            return firstNameInput;
        }
        if (field == AuthField.LAST_NAME) {
            return lastNameInput;
        }
        if (field == AuthField.DATE_OF_BIRTH) {
            return dateOfBirthInput;
        }
        if (field == AuthField.EMAIL) {
            return emailInput;
        }
        if (field == AuthField.PASSWORD) {
            return passwordInput;
        }
        return null;
    }

    private void renderAuthState(
            Resource<User> state,
            EditText firstNameInput,
            EditText lastNameInput,
            EditText dateOfBirthInput,
            EditText emailInput,
            EditText passwordInput,
            Button registerButton,
            Button goToLoginButton
    ) {
        if (state == null) {
            return;
        }

        if (state.status == Resource.Status.LOADING) {
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
            return;
        }

        if (state.status == Resource.Status.ERROR) {
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
            emailInput.setError(state.message);
            emailInput.requestFocus();
            return;
        }

        if (state.status == Resource.Status.SUCCESS && state.data != null) {
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

        Toast.makeText(requireContext(), "Account created for " + user.getFullName(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
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
