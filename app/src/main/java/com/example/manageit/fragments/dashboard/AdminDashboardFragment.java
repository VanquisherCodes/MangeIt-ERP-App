package com.example.manageit.fragments.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.manageit.R;
import com.example.manageit.activities.GroupAnnouncementsActivity;
import com.example.manageit.activities.GroupTasksActivity;
import com.example.manageit.activities.MembershipManagementActivity;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Role;
import com.example.manageit.models.User;
import com.example.manageit.utils.GreetingUtils;

/**
 * Dashboard for an admin membership inside a selected student group.
 */
public class AdminDashboardFragment extends Fragment {

    private static final String ARG_GROUP_ID = "arg_group_id";
    private static final String ARG_GROUP_NAME = "arg_group_name";

    public AdminDashboardFragment() {
        super(R.layout.fragment_dashboard_admin);
    }

    public static AdminDashboardFragment newInstance(String groupId, String groupName) {
        AdminDashboardFragment fragment = new AdminDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        args.putString(ARG_GROUP_NAME, groupName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String groupId = args != null ? args.getString(ARG_GROUP_ID, "") : "";
        String groupName = args != null ? args.getString(ARG_GROUP_NAME, "Student Group") : "Student Group";
        User currentUser = new SessionManager(requireContext()).getCurrentUser();

        ((TextView) view.findViewById(R.id.tv_dashboard_group_label)).setText(groupName);
        ((TextView) view.findViewById(R.id.tv_dashboard_greeting)).setText(GreetingUtils.getGreetingForCurrentTime());
        ((TextView) view.findViewById(R.id.tv_dashboard_name)).setText(GreetingUtils.getDisplayFirstName(currentUser));
        ((TextView) view.findViewById(R.id.tv_dashboard_membership_role)).setText("Group role: Admin");
        ((TextView) view.findViewById(R.id.tv_dashboard_avatar_initial)).setText(GreetingUtils.getInitials(currentUser));

        Button manageTasksButton = view.findViewById(R.id.btn_open_group_tasks);
        manageTasksButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), GroupTasksActivity.class);
            intent.putExtra(GroupTasksActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(GroupTasksActivity.EXTRA_GROUP_NAME, groupName);
            intent.putExtra(GroupTasksActivity.EXTRA_GROUP_ROLE, Role.ADMIN.name());
            startActivity(intent);
        });

        Button manageAnnouncementsButton = view.findViewById(R.id.btn_open_group_announcements);
        manageAnnouncementsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), GroupAnnouncementsActivity.class);
            intent.putExtra(GroupAnnouncementsActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(GroupAnnouncementsActivity.EXTRA_GROUP_NAME, groupName);
            intent.putExtra(GroupAnnouncementsActivity.EXTRA_GROUP_ROLE, Role.ADMIN.name());
            startActivity(intent);
        });

        Button manageMembershipButton = view.findViewById(R.id.btn_manage_membership_flow);
        manageMembershipButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MembershipManagementActivity.class);
            intent.putExtra(MembershipManagementActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(MembershipManagementActivity.EXTRA_GROUP_NAME, groupName);
            startActivity(intent);
        });
    }
}
