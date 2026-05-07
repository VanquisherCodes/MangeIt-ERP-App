package com.example.manageit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.manageit.R;
import com.example.manageit.adapters.GroupAnnouncementAdapter;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Announcement;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Role;
import com.example.manageit.repository.GroupAnnouncementsRepository;
import com.example.manageit.repository.GroupMembershipRepository;
import com.example.manageit.repository.RepositoryCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * Shared group announcements screen. Admins can publish, users can read.
 */
public class GroupAnnouncementsActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";
    public static final String EXTRA_GROUP_ROLE = "extra_group_role";

    private GroupAnnouncementsRepository groupAnnouncementsRepository;
    private GroupMembershipRepository groupMembershipRepository;
    private GroupAnnouncementAdapter adapter;
    private String groupId;
    private String groupName;
    private Role role;
    private GroupMembership currentMembership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_group_announcements);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
        role = Role.from(getIntent().getStringExtra(EXTRA_GROUP_ROLE));
        if (groupId == null || groupName == null) {
            finish();
            return;
        }

        groupAnnouncementsRepository = new GroupAnnouncementsRepository();
        groupMembershipRepository = new GroupMembershipRepository();

        bindChrome();
        bindList();
        bindForm();
        loadMembershipAndAnnouncements(sessionManager.getUserId());
    }

    private void bindChrome() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_group_announcements);
        toolbar.setTitle(groupName);

        Button backButton = findViewById(R.id.btn_back_from_group_announcements);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        TextView subtitle = findViewById(R.id.tv_group_announcements_subtitle);
        subtitle.setText(role == Role.ADMIN
                ? "Publish announcements for this group."
                : "Read announcements published for this group.");
    }

    private void bindList() {
        ListView listView = findViewById(R.id.lv_group_announcements);
        adapter = new GroupAnnouncementAdapter(this, new GroupAnnouncementAdapter.Listener() {
            @Override
            public void onEdit(Announcement announcement) {
                showEditDialog(announcement);
            }

            @Override
            public void onDelete(Announcement announcement) {
                confirmDelete(announcement);
            }
        });
        adapter.setCurrentRole(role);
        listView.setAdapter(adapter);

        Button retryButton = findViewById(R.id.btn_retry_group_announcements);
        retryButton.setOnClickListener(v -> loadAnnouncements());
    }

    private void bindForm() {
        View form = findViewById(R.id.layout_group_announcements_form);
        form.setVisibility(role == Role.ADMIN ? View.VISIBLE : View.GONE);

        Button createButton = findViewById(R.id.btn_create_group_announcement);
        createButton.setOnClickListener(v -> submitAnnouncement());
    }

    private void loadMembershipAndAnnouncements(String userId) {
        if (role == Role.ADMIN) {
            groupMembershipRepository.getMembership(userId, groupId, new RepositoryCallback<GroupMembership>() {
                @Override
                public void onSuccess(GroupMembership result) {
                    currentMembership = result;
                    loadAnnouncements();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(GroupAnnouncementsActivity.this, message, Toast.LENGTH_LONG).show();
                    loadAnnouncements();
                }
            });
        } else {
            loadAnnouncements();
        }
    }

    private void loadAnnouncements() {
        setLoadingState(true, null);
        groupAnnouncementsRepository.getGroupAnnouncements(groupId, new RepositoryCallback<List<Announcement>>() {
            @Override
            public void onSuccess(List<Announcement> result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.submitAnnouncements(result);
                setLoadingState(false, result == null || result.isEmpty() ? "No announcements have been published for this group yet." : null);
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.submitAnnouncements(null);
                setLoadingState(false, message);
            }
        });
    }

    private void submitAnnouncement() {
        EditText titleInput = findViewById(R.id.et_group_announcement_title);
        EditText messageInput = findViewById(R.id.et_group_announcement_message);
        Button createButton = findViewById(R.id.btn_create_group_announcement);

        String title = titleInput.getText().toString().trim();
        String message = messageInput.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleInput.setError("Enter an announcement title.");
            titleInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(message)) {
            messageInput.setError("Enter an announcement message.");
            messageInput.requestFocus();
            return;
        }
        if (currentMembership == null || currentMembership.getMembershipId() == null || currentMembership.getMembershipId().isEmpty()) {
            Toast.makeText(this, "Your admin membership could not be resolved yet.", Toast.LENGTH_LONG).show();
            return;
        }

        createButton.setEnabled(false);
        createButton.setText("Publishing...");
        groupAnnouncementsRepository.createAnnouncement(
                groupId,
                title,
                message,
                currentMembership.getMembershipId(),
                new RepositoryCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        titleInput.setText("");
                        messageInput.setText("");
                        createButton.setEnabled(true);
                        createButton.setText("Publish Announcement");
                        Toast.makeText(GroupAnnouncementsActivity.this, "Announcement published.", Toast.LENGTH_SHORT).show();
                        loadAnnouncements();
                    }

                    @Override
                    public void onError(String message) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        createButton.setEnabled(true);
                        createButton.setText("Publish Announcement");
                        Toast.makeText(GroupAnnouncementsActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
                );
    }

    private void showEditDialog(Announcement announcement) {
        EditText titleInput = new EditText(this);
        titleInput.setHint("Announcement title");
        titleInput.setText(announcement.getTitle() == null ? "" : announcement.getTitle());
        styleDialogInput(titleInput);

        EditText messageInput = new EditText(this);
        messageInput.setHint("Announcement message");
        messageInput.setMinLines(3);
        messageInput.setGravity(android.view.Gravity.TOP | android.view.Gravity.START);
        messageInput.setText(announcement.getBody() == null ? "" : announcement.getBody());
        styleDialogInput(messageInput);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding / 2);
        layout.addView(titleInput);
        layout.addView(messageInput);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Edit Announcement")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> updateAnnouncement(announcement, titleInput, messageInput))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void styleDialogInput(EditText input) {
        input.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        input.setHintTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
    }

    private void updateAnnouncement(Announcement announcement, EditText titleInput, EditText messageInput) {
        String announcementId = announcement.getId();
        if (announcementId == null || announcementId.trim().isEmpty()) {
            Toast.makeText(this, "This announcement cannot be edited right now.", Toast.LENGTH_LONG).show();
            return;
        }

        String title = titleInput.getText().toString().trim();
        String message = messageInput.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Title is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Message is required.", Toast.LENGTH_LONG).show();
            return;
        }

        groupAnnouncementsRepository.updateAnnouncement(announcementId, title, message, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(GroupAnnouncementsActivity.this, "Announcement updated.", Toast.LENGTH_SHORT).show();
                loadAnnouncements();
            }

            @Override
            public void onError(String errorMessage) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(GroupAnnouncementsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmDelete(Announcement announcement) {
        String announcementId = announcement.getId();
        if (announcementId == null || announcementId.trim().isEmpty()) {
            Toast.makeText(this, "This announcement cannot be deleted right now.", Toast.LENGTH_LONG).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(announcement.getTitle() == null ? "Delete announcement" : announcement.getTitle())
                .setMessage("Delete this announcement from the group?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAnnouncement(announcementId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAnnouncement(String announcementId) {
        groupAnnouncementsRepository.deleteAnnouncement(announcementId, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(GroupAnnouncementsActivity.this, "Announcement deleted.", Toast.LENGTH_SHORT).show();
                loadAnnouncements();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(GroupAnnouncementsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean loading, String message) {
        View progressView = findViewById(R.id.progress_group_announcements);
        ListView listView = findViewById(R.id.lv_group_announcements);
        TextView messageView = findViewById(R.id.tv_group_announcements_message);
        Button retryButton = findViewById(R.id.btn_retry_group_announcements);

        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        listView.setVisibility(!loading && message == null ? View.VISIBLE : View.GONE);
        messageView.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        retryButton.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        messageView.setText(message == null ? "" : message);
    }
}
