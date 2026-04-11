package com.example.manageit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manageit.R;
import com.example.manageit.adapters.GroupMemberAdapter;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.GroupMember;
import com.example.manageit.models.Role;
import com.example.manageit.repository.GroupAdminRepository;
import com.example.manageit.repository.RepositoryCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * Admin screen for managing role assignments inside one student group.
 */
public class MembershipManagementActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";

    private GroupAdminRepository groupAdminRepository;
    private GroupMemberAdapter adapter;
    private String groupId;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_membership_management);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
        if (groupId == null || groupName == null) {
            finish();
            return;
        }

        groupAdminRepository = new GroupAdminRepository();

        bindChrome(groupName);
        bindList(sessionManager.getUserId());
        bindRetry();
        loadMembers();
    }

    private void bindChrome(String title) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_membership_management);
        toolbar.setTitle(title);

        Button backButton = findViewById(R.id.btn_back_from_memberships);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void bindList(String currentUserId) {
        ListView listView = findViewById(R.id.lv_group_members);
        adapter = new GroupMemberAdapter(this, currentUserId, member -> showRoleChangeDialog(member));
        listView.setAdapter(adapter);
    }

    private void bindRetry() {
        Button retryButton = findViewById(R.id.btn_retry_memberships);
        retryButton.setOnClickListener(v -> loadMembers());
    }

    private void loadMembers() {
        setLoadingState(true, null);
        groupAdminRepository.getGroupMembers(groupId, new RepositoryCallback<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.submitMembers(result);
                setLoadingState(false, result == null || result.isEmpty() ? "No members found for this group yet." : null);
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.submitMembers(null);
                setLoadingState(false, message);
            }
        });
    }

    private void showRoleChangeDialog(GroupMember member) {
        Role targetRole = member.getRoleInGroup() == Role.ADMIN ? Role.USER : Role.ADMIN;
        String actionLabel = targetRole == Role.ADMIN ? "make this member an admin" : "remove admin access";

        new MaterialAlertDialogBuilder(this)
                .setTitle(member.getDisplayName())
                .setMessage("Do you want to " + actionLabel + " in " + groupName + "?")
                .setPositiveButton("Confirm", (dialog, which) -> updateRole(member, targetRole))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateRole(GroupMember member, Role targetRole) {
        adapter.markUpdating(member.getMembershipId(), true);
        groupAdminRepository.updateMemberRole(member, targetRole, new RepositoryCallback<GroupMember>() {
            @Override
            public void onSuccess(GroupMember result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.markUpdating(member.getMembershipId(), false);
                adapter.notifyDataSetChanged();
                Toast.makeText(
                        MembershipManagementActivity.this,
                        result.getDisplayName() + " is now " + (result.getRoleInGroup() == Role.ADMIN ? "an admin." : "a user."),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.markUpdating(member.getMembershipId(), false);
                Toast.makeText(MembershipManagementActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean loading, String message) {
        View progressView = findViewById(R.id.progress_memberships);
        ListView listView = findViewById(R.id.lv_group_members);
        TextView emptyView = findViewById(R.id.tv_memberships_message);
        Button retryButton = findViewById(R.id.btn_retry_memberships);

        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        listView.setVisibility(!loading && message == null ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        retryButton.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        emptyView.setText(message == null ? "" : message);
    }
}
