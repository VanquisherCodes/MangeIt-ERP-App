package com.example.manageit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manageit.R;
import com.example.manageit.adapters.GroupTaskAdapter;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.GroupMember;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Role;
import com.example.manageit.models.Task;
import com.example.manageit.repository.GroupAdminRepository;
import com.example.manageit.repository.GroupMembershipRepository;
import com.example.manageit.repository.GroupTasksRepository;
import com.example.manageit.repository.RepositoryCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared group task screen. Admins can create tasks, users can read them.
 */
public class GroupTasksActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";
    public static final String EXTRA_GROUP_ROLE = "extra_group_role";

    private GroupTasksRepository groupTasksRepository;
    private GroupAdminRepository groupAdminRepository;
    private GroupMembershipRepository groupMembershipRepository;
    private GroupTaskAdapter adapter;
    private String groupId;
    private String groupName;
    private Role role;
    private GroupMembership currentMembership;
    private final List<GroupMember> groupMembers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_group_tasks);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
        role = Role.from(getIntent().getStringExtra(EXTRA_GROUP_ROLE));
        if (groupId == null || groupName == null) {
            finish();
            return;
        }

        groupTasksRepository = new GroupTasksRepository();
        groupAdminRepository = new GroupAdminRepository();
        groupMembershipRepository = new GroupMembershipRepository();

        bindChrome();
        bindList();
        bindForm();
        loadMembershipAndContent(sessionManager.getUserId());
    }

    private void bindChrome() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_group_tasks);
        toolbar.setTitle(groupName);

        Button backButton = findViewById(R.id.btn_back_from_group_tasks);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        TextView subtitle = findViewById(R.id.tv_group_tasks_subtitle);
        subtitle.setText(role == Role.ADMIN
                ? "Create tasks for this group and oversee progress."
                : "View group tasks and update the ones assigned to you.");
    }

    private void bindList() {
        ListView listView = findViewById(R.id.lv_group_tasks);
        adapter = new GroupTaskAdapter(this, this::showStatusDialog);
        adapter.setCurrentRole(role);
        listView.setAdapter(adapter);

        Button retryButton = findViewById(R.id.btn_retry_group_tasks);
        retryButton.setOnClickListener(v -> loadTasks());
    }

    private void bindForm() {
        View form = findViewById(R.id.layout_group_tasks_form);
        form.setVisibility(role == Role.ADMIN ? View.VISIBLE : View.GONE);

        Button createButton = findViewById(R.id.btn_create_group_task);
        createButton.setOnClickListener(v -> submitTask());
    }

    private void loadMembershipAndContent(String userId) {
        groupMembershipRepository.getMembership(userId, groupId, new RepositoryCallback<GroupMembership>() {
            @Override
            public void onSuccess(GroupMembership result) {
                currentMembership = result;
                adapter.setCurrentMembershipId(result == null ? "" : result.getMembershipId());
                loadMembers();
                loadTasks();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(GroupTasksActivity.this, message, Toast.LENGTH_LONG).show();
                adapter.setCurrentMembershipId("");
                loadMembers();
                loadTasks();
            }
        });
    }

    private void loadMembers() {
        groupAdminRepository.getGroupMembers(groupId, new RepositoryCallback<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> result) {
                groupMembers.clear();
                if (result != null) {
                    groupMembers.addAll(result);
                }
                applyMemberMappings();
                bindAssignmentSpinner();
            }

            @Override
            public void onError(String message) {
                bindAssignmentSpinner();
            }
        });
    }

    private void applyMemberMappings() {
        Map<String, String> names = new LinkedHashMap<>();
        for (GroupMember member : groupMembers) {
            names.put(member.getMembershipId(), member.getDisplayName());
        }
        adapter.setMemberNamesByMembershipId(names);
    }

    private void bindAssignmentSpinner() {
        Spinner spinner = findViewById(R.id.spinner_group_task_assignee);
        List<String> labels = new ArrayList<>();
        for (GroupMember member : groupMembers) {
            labels.add(member.getDisplayName() + " (" + member.getRoleInGroup().name().toLowerCase() + ")");
        }
        if (labels.isEmpty()) {
            labels.add("No members available");
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setEnabled(!groupMembers.isEmpty() && role == Role.ADMIN);
    }

    private void loadTasks() {
        setLoadingState(true, null);
        groupTasksRepository.getGroupTasks(groupId, new RepositoryCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.submitTasks(result);
                setLoadingState(false, result == null || result.isEmpty() ? "No tasks have been added for this group yet." : null);
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.submitTasks(null);
                setLoadingState(false, message);
            }
        });
    }

    private void submitTask() {
        EditText titleInput = findViewById(R.id.et_group_task_title);
        EditText descriptionInput = findViewById(R.id.et_group_task_description);
        EditText dueDateInput = findViewById(R.id.et_group_task_due_date);
        Spinner assigneeSpinner = findViewById(R.id.spinner_group_task_assignee);
        Button createButton = findViewById(R.id.btn_create_group_task);

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String dueDate = dueDateInput.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleInput.setError("Enter a task title.");
            titleInput.requestFocus();
            return;
        }
        if (groupMembers.isEmpty()) {
            Toast.makeText(this, "No members are available to assign this task to.", Toast.LENGTH_LONG).show();
            return;
        }
        if (currentMembership == null || currentMembership.getMembershipId() == null || currentMembership.getMembershipId().isEmpty()) {
            Toast.makeText(this, "Your admin membership could not be resolved yet.", Toast.LENGTH_LONG).show();
            return;
        }

        GroupMember assignee = groupMembers.get(Math.max(0, assigneeSpinner.getSelectedItemPosition()));
        createButton.setEnabled(false);
        createButton.setText("Creating...");

        groupTasksRepository.createTask(
                groupId,
                title,
                description,
                dueDate,
                assignee.getMembershipId(),
                currentMembership.getMembershipId(),
                new RepositoryCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        titleInput.setText("");
                        descriptionInput.setText("");
                        dueDateInput.setText("");
                        createButton.setEnabled(true);
                        createButton.setText("Create Group Task");
                        Toast.makeText(GroupTasksActivity.this, "Group task created.", Toast.LENGTH_SHORT).show();
                        loadTasks();
                    }

                    @Override
                    public void onError(String message) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        createButton.setEnabled(true);
                        createButton.setText("Create Group Task");
                        Toast.makeText(GroupTasksActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void showStatusDialog(Task task) {
        String[] statuses = {"pending", "in_progress", "completed", "cancelled"};
        new MaterialAlertDialogBuilder(this)
                .setTitle(task.getTitle())
                .setMessage(role == Role.ADMIN
                        ? "Choose the new status for this task."
                        : "Choose the new status for your assigned task.")
                .setItems(statuses, (dialog, which) -> updateTaskStatus(task, statuses[which]))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTaskStatus(Task task, String status) {
        groupTasksRepository.updateTaskStatus(task.getId(), status, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                Toast.makeText(GroupTasksActivity.this, "Task status updated.", Toast.LENGTH_SHORT).show();
                loadTasks();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                Toast.makeText(GroupTasksActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean loading, String message) {
        View progressView = findViewById(R.id.progress_group_tasks);
        ListView listView = findViewById(R.id.lv_group_tasks);
        TextView messageView = findViewById(R.id.tv_group_tasks_message);
        Button retryButton = findViewById(R.id.btn_retry_group_tasks);

        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        listView.setVisibility(!loading && message == null ? View.VISIBLE : View.GONE);
        messageView.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        retryButton.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        messageView.setText(message == null ? "" : message);
    }
}
