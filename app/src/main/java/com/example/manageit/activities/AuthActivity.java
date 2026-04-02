package com.example.manageit.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manageit.R;
import com.example.manageit.fragments.auth.LoginFragment;

/**
 * Hosts authentication fragments (login/register).
 */
public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_auth, new LoginFragment())
                    .commit();
        }
    }
}
