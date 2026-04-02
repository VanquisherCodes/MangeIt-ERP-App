package com.example.manageit.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.manageit.R;
import com.example.manageit.activities.MainActivity;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Role;

/**
 * Login screen placeholder. Replace click handlers with API integration later.
 */
public class LoginFragment extends Fragment {

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button loginButton = view.findViewById(R.id.btn_login);
        Button goToRegisterButton = view.findViewById(R.id.btn_go_register);

        loginButton.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(requireContext());
            sessionManager.createSession("demo_user", Role.USER);

            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
        });

        goToRegisterButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_auth, new RegisterFragment())
                .addToBackStack(null)
                .commit());
    }
}
