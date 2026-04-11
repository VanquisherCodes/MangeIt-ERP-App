package com.example.manageit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manageit.R;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Role;
import com.example.manageit.navigation.RoleNavigator;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Hosts the dashboard for the selected student group membership.
 */
public class GroupDashboardActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";
    public static final String EXTRA_GROUP_ROLE = "extra_group_role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!new SessionManager(this).isLoggedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_group_dashboard);

        String groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        String groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
        Role role = Role.from(getIntent().getStringExtra(EXTRA_GROUP_ROLE));

        if (groupId == null || groupName == null) {
            finish();
            return;
        }

        bindChrome(groupName);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_group_dashboard, RoleNavigator.getDashboardFragment(role, groupId, groupName))
                    .commit();
        }
    }

    private void bindChrome(String groupName) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_group_dashboard);
        toolbar.setTitle(groupName);

        Button backButton = findViewById(R.id.btn_back_to_groups);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }
}
