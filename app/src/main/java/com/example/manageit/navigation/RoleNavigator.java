package com.example.manageit.navigation;

import androidx.fragment.app.Fragment;

import com.example.manageit.fragments.dashboard.AdminDashboardFragment;
import com.example.manageit.fragments.dashboard.UserDashboardFragment;
import com.example.manageit.models.Role;

/**
 * Resolves entry fragment based on authenticated role.
 */
public final class RoleNavigator {

    private RoleNavigator() {
    }

    public static Fragment getDashboardFragment(Role role, String groupId, String groupName) {
        return role == Role.ADMIN
                ? AdminDashboardFragment.newInstance(groupId, groupName)
                : UserDashboardFragment.newInstance(groupId, groupName);
    }
}
