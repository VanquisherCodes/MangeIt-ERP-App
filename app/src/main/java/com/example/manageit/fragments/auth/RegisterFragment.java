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
 * Registration screen placeholder. Replace demo behavior with real API calls.
 */
public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        super(R.layout.fragment_register);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button registerButton = view.findViewById(R.id.btn_register);
        Button goToLoginButton = view.findViewById(R.id.btn_go_login);

        registerButton.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(requireContext());
            sessionManager.createSession("new_user", Role.USER);

            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
        });

        goToLoginButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .popBackStack());
    }
}
