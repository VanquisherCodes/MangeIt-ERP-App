package com.example.manageit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manageit.R;
import com.example.manageit.adapters.GroupListAdapter;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Request;
import com.example.manageit.models.Role;
import com.example.manageit.models.StudentGroup;
import com.example.manageit.models.User;
import com.example.manageit.repository.AdminAccessRequestRepository;
import com.example.manageit.repository.GroupMembershipRepository;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.repository.StudentGroupRepository;
import com.example.manageit.utils.GreetingUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * Post-login landing page that lists available student groups.
 */
public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private GroupMembershipRepository membershipRepository;
    private StudentGroupRepository studentGroupRepository;
    private AdminAccessRequestRepository adminAccessRequestRepository;
    private GroupListAdapter groupListAdapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        currentUser = sessionManager.getCurrentUser();
        if (currentUser.getId() == null || currentUser.getId().trim().isEmpty()) {
            sessionManager.clearSession();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        membershipRepository = new GroupMembershipRepository();
        studentGroupRepository = new StudentGroupRepository();
        adminAccessRequestRepository = new AdminAccessRequestRepository(this);

        bindHeader();
        bindGroupList();
        bindLogout();
        loadGroups();
    }

    private void bindHeader() {
        TextView titleView = findViewById(R.id.tv_main_greeting);
        TextView subtitleView = findViewById(R.id.tv_main_user_name);

        titleView.setText(GreetingUtils.getGreetingForCurrentTime());
        subtitleView.setText(currentUser.getFullName());
    }

    private void bindGroupList() {
        ListView listView = findViewById(R.id.lv_groups);
        groupListAdapter = new GroupListAdapter(this, new GroupListAdapter.Listener() {
            @Override
            public void onRequestUserEnrollment(StudentGroup group) {
                requestUserEnrollment(group);
            }
        });
        listView.setAdapter(groupListAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> handleGroupTap(groupListAdapter.getItem(position)));
    }

    private void bindLogout() {
        ImageButton logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });
    }

    private void loadGroups() {
        studentGroupRepository.getAvailableGroups(new RepositoryCallback<List<StudentGroup>>() {
            @Override
            public void onSuccess(List<StudentGroup> result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                groupListAdapter.submitGroups(result);
                if (result == null) {
                    return;
                }
                for (StudentGroup group : result) {
                    resolveMembership(group);
                    resolveEnrollmentRequest(group);
                }
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resolveMembership(StudentGroup group) {
        groupListAdapter.markMembershipLoading(group.getGroupId());
        membershipRepository.getMembership(currentUser.getId(), group.getGroupId(), new RepositoryCallback<GroupMembership>() {
            @Override
            public void onSuccess(GroupMembership result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                groupListAdapter.updateMembership(group.getGroupId(), result);
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                groupListAdapter.markMembershipError(group.getGroupId());
            }
        });
    }

    private void resolveEnrollmentRequest(StudentGroup group) {
        groupListAdapter.markEnrollmentRequestLoading(group.getGroupId());
        adminAccessRequestRepository.getUserAdminAccessRequest(
                currentUser.getId(),
                group.getGroupId(),
                new RepositoryCallback<Request>() {
                    @Override
                    public void onSuccess(Request result) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        groupListAdapter.updateEnrollmentRequest(group.getGroupId(), result);
                    }

                    @Override
                    public void onError(String message) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        groupListAdapter.markEnrollmentRequestError(group.getGroupId());
                    }
                }
        );
    }

    private void handleGroupTap(StudentGroup group) {
        if (groupListAdapter.isBusy(group.getGroupId())) {
            Toast.makeText(this, "Please wait while we finish this request.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (groupListAdapter.hasMembershipError(group.getGroupId())) {
            resolveMembership(group);
            resolveEnrollmentRequest(group);
            Toast.makeText(this, "Retrying group access lookup...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!groupListAdapter.isMembershipResolved(group.getGroupId())) {
            Toast.makeText(this, "Still checking your membership for this group.", Toast.LENGTH_SHORT).show();
            return;
        }

        GroupMembership membership = groupListAdapter.getMembershipForGroup(group.getGroupId());
        if (membership != null) {
            openDashboard(group, membership.getRoleInGroup());
            return;
        }

        if (groupListAdapter.hasPendingEnrollmentRequest(group.getGroupId())) {
            Toast.makeText(this, "Your enrollment request is pending admin approval.", Toast.LENGTH_LONG).show();
            return;
        }

        if (groupListAdapter.hasApprovedEnrollmentRequest(group.getGroupId())) {
            resolveMembership(group);
            Toast.makeText(this, "Enrollment approved. Syncing your membership...", Toast.LENGTH_LONG).show();
            return;
        }

        if (groupListAdapter.hasRejectedEnrollmentRequest(group.getGroupId())) {
            Toast.makeText(this, "Your enrollment request was rejected. You can submit again.", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Tap 'Join As User' to request access to this group.", Toast.LENGTH_LONG).show();
    }

    private void requestUserEnrollment(StudentGroup group) {
        if (groupListAdapter.isBusy(group.getGroupId())) {
            Toast.makeText(this, "Please wait while we finish this request.", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(group.getGroupName())
                .setMessage("Send a request to join this group as a standard user?")
                .setPositiveButton("Request Enrollment", (dialog, which) -> {
                    groupListAdapter.markEnrollmentRequestSubmitting(group.getGroupId());
                    adminAccessRequestRepository.createAdminAccessRequest(
                            currentUser.getId(),
                            group.getGroupId(),
                            "requested_user_enrollment",
                            currentUser.getFirstName(),
                            currentUser.getLastName(),
                            currentUser.getEmail(),
                            new RepositoryCallback<Request>() {
                                @Override
                                public void onSuccess(Request result) {
                                    if (isFinishing() || isDestroyed()) {
                                        return;
                                    }

                                    groupListAdapter.updateEnrollmentRequest(group.getGroupId(), result);
                                    Toast.makeText(
                                            MainActivity.this,
                                            "Enrollment request submitted. Wait for admin approval.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }

                                @Override
                                public void onError(String message) {
                                    if (isFinishing() || isDestroyed()) {
                                        return;
                                    }

                                    groupListAdapter.markEnrollmentRequestError(group.getGroupId());
                                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openDashboard(StudentGroup group, Role role) {
        Intent intent = new Intent(this, GroupDashboardActivity.class);
        intent.putExtra(GroupDashboardActivity.EXTRA_GROUP_ID, group.getGroupId());
        intent.putExtra(GroupDashboardActivity.EXTRA_GROUP_NAME, group.getGroupName());
        intent.putExtra(GroupDashboardActivity.EXTRA_GROUP_ROLE, role.name());
        startActivity(intent);
    }
}
