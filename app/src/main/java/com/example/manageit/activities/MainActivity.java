package com.example.manageit.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manageit.R;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Role;
import com.example.manageit.navigation.RoleNavigator;

/**
 * Host activity for role-based ERP dashboard and module navigation.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            SessionManager sessionManager = new SessionManager(this);
            Role role = sessionManager.getRole();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_main, RoleNavigator.getHomeFragment(role))
                    .commit();
        }
    }
}
