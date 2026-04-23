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
import com.example.manageit.activities.GroupBudgetActivity;
import com.example.manageit.activities.GroupEventsActivity;
import com.example.manageit.activities.GroupTasksActivity;
import com.example.manageit.activities.MembershipManagementActivity;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Event;
import com.example.manageit.models.Request;
import com.example.manageit.models.Role;
import com.example.manageit.models.Task;
import com.example.manageit.models.User;
import com.example.manageit.repository.AdminAccessRequestRepository;
import com.example.manageit.repository.GroupAdminRepository;
import com.example.manageit.repository.GroupEventsRepository;
import com.example.manageit.repository.GroupTasksRepository;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.utils.GreetingUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Dashboard for an admin membership inside a selected student group.
 */
public class AdminDashboardFragment extends Fragment {

    private static final String ARG_GROUP_ID = "arg_group_id";
    private static final String ARG_GROUP_NAME = "arg_group_name";

    private final GroupAdminRepository groupAdminRepository = new GroupAdminRepository();
    private final GroupTasksRepository groupTasksRepository = new GroupTasksRepository();
    private final GroupEventsRepository groupEventsRepository = new GroupEventsRepository();
    private AdminAccessRequestRepository adminAccessRequestRepository;
    private @Nullable View rootView;
    private String currentGroupId = "";
    private final List<com.example.manageit.models.GroupMember> latestGroupMembers = new LinkedList<>();

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
        rootView = view;

        adminAccessRequestRepository = new AdminAccessRequestRepository(requireContext());

        Bundle args = getArguments();
        String groupId = args != null ? args.getString(ARG_GROUP_ID, "") : "";
        currentGroupId = groupId;
        String groupName = args != null ? args.getString(ARG_GROUP_NAME, "Student Group") : "Student Group";
        User currentUser = new SessionManager(requireContext()).getCurrentUser();

        ((TextView) view.findViewById(R.id.tv_dashboard_group_label)).setText(groupName);
        ((TextView) view.findViewById(R.id.tv_dashboard_greeting)).setText(GreetingUtils.getGreetingForCurrentTime());
        ((TextView) view.findViewById(R.id.tv_dashboard_name)).setText(GreetingUtils.getDisplayFirstName(currentUser));
        ((TextView) view.findViewById(R.id.tv_dashboard_membership_role)).setText("Group role: Admin");
        ((TextView) view.findViewById(R.id.tv_dashboard_avatar_initial)).setText(GreetingUtils.getInitials(currentUser));

        bindActions(view, groupId, groupName);
        bindKpiDefaults(view);
        loadKpis(view, groupId);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rootView != null && !currentGroupId.trim().isEmpty()) {
            loadKpis(rootView, currentGroupId);
        }
    }

    private void bindActions(View view, String groupId, String groupName) {
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

        Button manageEventsButton = view.findViewById(R.id.btn_open_group_events);
        manageEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), GroupEventsActivity.class);
            intent.putExtra(GroupEventsActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(GroupEventsActivity.EXTRA_GROUP_NAME, groupName);
            intent.putExtra(GroupEventsActivity.EXTRA_GROUP_ROLE, Role.ADMIN.name());
            startActivity(intent);
        });

        Button openBudgetButton = view.findViewById(R.id.btn_open_group_budget);
        openBudgetButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), GroupBudgetActivity.class);
            intent.putExtra(GroupBudgetActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(GroupBudgetActivity.EXTRA_GROUP_NAME, groupName);
            intent.putExtra(GroupBudgetActivity.EXTRA_GROUP_ROLE, Role.ADMIN.name());
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

    private void bindKpiDefaults(View view) {
        ((TextView) view.findViewById(R.id.tv_kpi_members_value)).setText("--");
        ((TextView) view.findViewById(R.id.tv_kpi_tasks_value)).setText("--");
        ((TextView) view.findViewById(R.id.tv_kpi_events_value)).setText("--");
        ((TextView) view.findViewById(R.id.tv_kpi_pending_requests_value)).setText("--");
    }

    private void loadKpis(View view, String groupId) {
        groupAdminRepository.getGroupMembers(groupId, new RepositoryCallback<List<com.example.manageit.models.GroupMember>>() {
            @Override
            public void onSuccess(List<com.example.manageit.models.GroupMember> result) {
                if (!isAdded()) {
                    return;
                }
                latestGroupMembers.clear();
                if (result != null) {
                    latestGroupMembers.addAll(result);
                }
                ((TextView) view.findViewById(R.id.tv_kpi_members_value)).setText(String.valueOf(result == null ? 0 : result.size()));
                refreshPendingRequestCount(view, groupId);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                latestGroupMembers.clear();
                ((TextView) view.findViewById(R.id.tv_kpi_members_value)).setText("N/A");
                refreshPendingRequestCount(view, groupId);
            }
        });

        groupTasksRepository.getGroupTasks(groupId, new RepositoryCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> result) {
                if (!isAdded()) {
                    return;
                }

                int total = result == null ? 0 : result.size();
                int completed = 0;
                if (result != null) {
                    for (Task task : result) {
                        if (task.isCompleted()) {
                            completed++;
                        }
                    }
                }
                ((TextView) view.findViewById(R.id.tv_kpi_tasks_value)).setText(completed + "/" + total);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                ((TextView) view.findViewById(R.id.tv_kpi_tasks_value)).setText("N/A");
            }
        });

        groupEventsRepository.getGroupEvents(groupId, new RepositoryCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> result) {
                if (!isAdded()) {
                    return;
                }
                ((TextView) view.findViewById(R.id.tv_kpi_events_value)).setText(String.valueOf(result == null ? 0 : result.size()));
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                ((TextView) view.findViewById(R.id.tv_kpi_events_value)).setText("N/A");
            }
        });

        refreshPendingRequestCount(view, groupId);
    }

    private void refreshPendingRequestCount(View view, String groupId) {
        adminAccessRequestRepository.getGroupAdminAccessRequests(groupId, new RepositoryCallback<List<Request>>() {
            @Override
            public void onSuccess(List<Request> result) {
                if (!isAdded()) {
                    return;
                }

                int pendingRequestCount = countVisiblePendingRequests(result, latestGroupMembers);

                ((TextView) view.findViewById(R.id.tv_kpi_pending_requests_value))
                        .setText(String.valueOf(pendingRequestCount));
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                ((TextView) view.findViewById(R.id.tv_kpi_pending_requests_value)).setText("N/A");
            }
        });
    }

    private int countVisiblePendingRequests(
            List<Request> requests,
            @Nullable List<com.example.manageit.models.GroupMember> members
    ) {
        if (requests == null || requests.isEmpty()) {
            return 0;
        }

        Set<String> memberUserIds = new HashSet<>();
        if (members != null) {
            for (com.example.manageit.models.GroupMember member : members) {
                if (member == null || member.getUserId() == null) {
                    continue;
                }
                memberUserIds.add(member.getUserId().trim());
            }
        }

        int count = 0;
        for (Request request : requests) {
            if (request == null || !request.isPending()) {
                continue;
            }

            String requesterId = request.getUserId() == null ? "" : request.getUserId().trim();
            if (!requesterId.isEmpty() && memberUserIds.contains(requesterId)) {
                continue;
            }

            count++;
        }
        return count;
    }
}
