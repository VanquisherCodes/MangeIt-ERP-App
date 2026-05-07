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
import com.example.manageit.adapters.AdminAccessRequestAdapter;
import com.example.manageit.adapters.GroupMemberAdapter;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.GroupMember;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Request;
import com.example.manageit.models.Role;
import com.example.manageit.repository.AdminAccessRequestRepository;
import com.example.manageit.repository.GroupAdminRepository;
import com.example.manageit.repository.GroupMembershipRepository;
import com.example.manageit.repository.RepositoryCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Admin screen for managing members and pending enrollment requests inside one group.
 */
public class MembershipManagementActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";

    private GroupAdminRepository groupAdminRepository;
    private AdminAccessRequestRepository adminAccessRequestRepository;
    private GroupMembershipRepository groupMembershipRepository;
    private GroupMemberAdapter memberAdapter;
    private AdminAccessRequestAdapter requestAdapter;
    private String groupId;
    private String groupName;
    private boolean waitingMembers;
    private boolean waitingRequests;
    private boolean membersLoadedSuccessfully;
    private boolean requestsLoadedSuccessfully;
    private final List<GroupMember> latestMembers = new ArrayList<>();
    private final List<Request> latestRequests = new ArrayList<>();

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
        adminAccessRequestRepository = new AdminAccessRequestRepository(this);
        groupMembershipRepository = new GroupMembershipRepository();

        bindChrome(groupName);
        bindLists(sessionManager.getUserId());
        bindRetry();
        loadData();
    }

    private void bindChrome(String title) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_membership_management);
        toolbar.setTitle(title);

        Button backButton = findViewById(R.id.btn_back_from_memberships);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void bindLists(String currentUserId) {
        ListView memberListView = findViewById(R.id.lv_group_members);
        memberAdapter = new GroupMemberAdapter(this, currentUserId, this::showRoleChangeDialog);
        memberListView.setAdapter(memberAdapter);

        ListView requestListView = findViewById(R.id.lv_admin_access_requests);
        requestAdapter = new AdminAccessRequestAdapter(this, new AdminAccessRequestAdapter.Listener() {
            @Override
            public void onApprove(Request request) {
                showRequestDecisionDialog(request, "approved");
            }

            @Override
            public void onReject(Request request) {
                showRequestDecisionDialog(request, "rejected");
            }
        });
        requestListView.setAdapter(requestAdapter);
    }

    private void bindRetry() {
        Button retryButton = findViewById(R.id.btn_retry_memberships);
        retryButton.setOnClickListener(v -> loadData());
    }

    private void loadData() {
        waitingMembers = true;
        waitingRequests = true;
        membersLoadedSuccessfully = false;
        requestsLoadedSuccessfully = false;
        latestMembers.clear();
        latestRequests.clear();
        setLoadingState(true, null);

        loadMembers();
        loadEnrollmentRequests();
    }

    private void loadMembers() {
        groupAdminRepository.getGroupMembers(groupId, new RepositoryCallback<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                waitingMembers = false;
                membersLoadedSuccessfully = true;
                latestMembers.clear();
                if (result != null) {
                    latestMembers.addAll(result);
                }
                memberAdapter.submitMembers(result);
                renderMembersSection(result, null);
                if (requestsLoadedSuccessfully) {
                    refreshRequestSection();
                }
                renderFinalLoadState();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                waitingMembers = false;
                membersLoadedSuccessfully = false;
                latestMembers.clear();
                memberAdapter.submitMembers(null);
                renderMembersSection(null, message);
                if (requestsLoadedSuccessfully) {
                    refreshRequestSection();
                }
                renderFinalLoadState();
            }
        });
    }

    private void loadEnrollmentRequests() {
        adminAccessRequestRepository.getGroupAdminAccessRequests(groupId, new RepositoryCallback<List<Request>>() {
            @Override
            public void onSuccess(List<Request> result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                waitingRequests = false;
                requestsLoadedSuccessfully = true;
                latestRequests.clear();
                if (result != null) {
                    latestRequests.addAll(result);
                }
                refreshRequestSection();
                renderFinalLoadState();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                waitingRequests = false;
                requestsLoadedSuccessfully = false;
                latestRequests.clear();
                requestAdapter.submitRequests(null);
                renderRequestsSection(null, message);
                renderFinalLoadState();
            }
        });
    }

    private void refreshRequestSection() {
        List<Request> visibleRequests = filterVisibleRequests(latestRequests, latestMembers);
        requestAdapter.submitRequests(visibleRequests);
        renderRequestsSection(visibleRequests, null);
    }

    private List<Request> filterVisibleRequests(List<Request> requests, List<GroupMember> members) {
        List<Request> visibleRequests = new ArrayList<>();
        Set<String> memberUserIds = new HashSet<>();

        if (members != null) {
            for (GroupMember member : members) {
                if (member == null || member.getUserId() == null) {
                    continue;
                }
                memberUserIds.add(member.getUserId().trim());
            }
        }

        if (requests == null) {
            return visibleRequests;
        }

        for (Request request : requests) {
            if (request == null || !request.isPending()) {
                continue;
            }

            String requesterId = request.getUserId() == null ? "" : request.getUserId().trim();
            if (!requesterId.isEmpty() && memberUserIds.contains(requesterId)) {
                continue;
            }

            visibleRequests.add(request);
        }

        return visibleRequests;
    }

    private void removeRequestFromLatestList(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return;
        }

        for (int index = latestRequests.size() - 1; index >= 0; index--) {
            Request request = latestRequests.get(index);
            if (request == null || request.getRequestId() == null) {
                continue;
            }

            if (requestId.trim().equals(request.getRequestId().trim())) {
                latestRequests.remove(index);
            }
        }

        refreshRequestSection();
    }

    private void renderFinalLoadState() {
        if (waitingMembers || waitingRequests) {
            return;
        }

        if (membersLoadedSuccessfully || requestsLoadedSuccessfully) {
            setLoadingState(false, null);
            return;
        }

        setLoadingState(false, "Couldn't load group members or enrollment requests right now.");
    }

    private void renderMembersSection(List<GroupMember> members, String errorMessage) {
        ListView listView = findViewById(R.id.lv_group_members);
        TextView messageView = findViewById(R.id.tv_members_section_message);

        if (errorMessage != null) {
            listView.setVisibility(View.GONE);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(errorMessage);
            return;
        }

        if (members == null || members.isEmpty()) {
            listView.setVisibility(View.GONE);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText("No members found for this group yet.");
            return;
        }

        listView.setVisibility(View.VISIBLE);
        messageView.setVisibility(View.GONE);
    }

    private void renderRequestsSection(List<Request> requests, String errorMessage) {
        ListView listView = findViewById(R.id.lv_admin_access_requests);
        TextView messageView = findViewById(R.id.tv_admin_requests_message);

        if (errorMessage != null) {
            listView.setVisibility(View.GONE);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(errorMessage);
            return;
        }

        if (requests == null || requests.isEmpty()) {
            listView.setVisibility(View.GONE);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText("No enrollment requests found.");
            return;
        }

        listView.setVisibility(View.VISIBLE);
        messageView.setVisibility(View.GONE);
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
        memberAdapter.markUpdating(member.getMembershipId(), true);
        groupAdminRepository.updateMemberRole(member, targetRole, new RepositoryCallback<GroupMember>() {
            @Override
            public void onSuccess(GroupMember result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                memberAdapter.markUpdating(member.getMembershipId(), false);
                Toast.makeText(
                        MembershipManagementActivity.this,
                        result.getDisplayName() + " is now " + (result.getRoleInGroup() == Role.ADMIN ? "an admin." : "a user."),
                        Toast.LENGTH_SHORT
                ).show();
                loadData();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                memberAdapter.markUpdating(member.getMembershipId(), false);
                Toast.makeText(MembershipManagementActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showRequestDecisionDialog(Request request, String targetStatus) {
        String actionLabel = "approved".equalsIgnoreCase(targetStatus) ? "approve" : "reject";
        new MaterialAlertDialogBuilder(this)
                .setTitle(request.getRequesterDisplayName())
                .setMessage("Do you want to " + actionLabel + " this enrollment request?")
                .setPositiveButton("Confirm", (dialog, which) -> updateRequestStatus(request, targetStatus))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateRequestStatus(Request request, String targetStatus) {
        String requestId = request.getRequestId();
        if (requestId == null || requestId.trim().isEmpty()) {
            Toast.makeText(this, "This request cannot be updated right now.", Toast.LENGTH_LONG).show();
            return;
        }

        requestAdapter.markUpdating(requestId, true);
        adminAccessRequestRepository.updateAdminAccessRequestStatus(requestId, targetStatus, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if ("approved".equalsIgnoreCase(targetStatus)) {
                    grantMembershipForRequest(request, requestId);
                    return;
                }

                removeRequestFromLatestList(requestId);
                requestAdapter.markUpdating(requestId, false);
                Toast.makeText(MembershipManagementActivity.this, "Request " + targetStatus + ".", Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                requestAdapter.markUpdating(requestId, false);
                Toast.makeText(MembershipManagementActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void grantMembershipForRequest(Request request, String requestId) {
        String requesterId = request.getUserId();
        if (requesterId == null || requesterId.trim().isEmpty()) {
            requestAdapter.markUpdating(requestId, false);
            Toast.makeText(this, "Request approved, but requester id is missing.", Toast.LENGTH_LONG).show();
            loadData();
            return;
        }

        groupMembershipRepository.getMembership(requesterId, groupId, new RepositoryCallback<GroupMembership>() {
            @Override
            public void onSuccess(GroupMembership membership) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (membership == null || membership.getMembershipId() == null || membership.getMembershipId().trim().isEmpty()) {
                    groupMembershipRepository.createMembership(requesterId, groupId, Role.USER, new RepositoryCallback<GroupMembership>() {
                        @Override
                        public void onSuccess(GroupMembership result) {
                            if (isFinishing() || isDestroyed()) {
                                return;
                            }
                            adminAccessRequestRepository.clearUserGroupRequestFromLocalStore(requesterId, groupId);
                            removeRequestFromLatestList(requestId);
                            requestAdapter.markUpdating(requestId, false);
                            Toast.makeText(MembershipManagementActivity.this, "Request approved and membership created.", Toast.LENGTH_SHORT).show();
                            loadData();
                        }

                        @Override
                        public void onError(String message) {
                            if (isFinishing() || isDestroyed()) {
                                return;
                            }
                            requestAdapter.markUpdating(requestId, false);
                            Toast.makeText(MembershipManagementActivity.this, "Request approved, but membership creation failed: " + message, Toast.LENGTH_LONG).show();
                            loadData();
                        }
                    });
                    return;
                }

                adminAccessRequestRepository.clearUserGroupRequestFromLocalStore(requesterId, groupId);
                removeRequestFromLatestList(requestId);
                requestAdapter.markUpdating(requestId, false);
                Toast.makeText(MembershipManagementActivity.this, "Request approved. Membership already exists.", Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                requestAdapter.markUpdating(requestId, false);
                Toast.makeText(MembershipManagementActivity.this, "Request approved, but membership lookup failed: " + message, Toast.LENGTH_LONG).show();
                loadData();
            }
        });
    }

    private void setLoadingState(boolean loading, String message) {
        View progressView = findViewById(R.id.progress_memberships);
        View contentView = findViewById(R.id.layout_membership_content);
        TextView emptyView = findViewById(R.id.tv_memberships_message);
        Button retryButton = findViewById(R.id.btn_retry_memberships);

        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        contentView.setVisibility(!loading && message == null ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        retryButton.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        emptyView.setText(message == null ? "" : message);
    }
}
