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
import com.example.manageit.adapters.GroupEventAdapter;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Event;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Role;
import com.example.manageit.repository.GroupEventsRepository;
import com.example.manageit.repository.GroupMembershipRepository;
import com.example.manageit.repository.RepositoryCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Shared group events screen. Admins can manage events, users can view them.
 */
public class GroupEventsActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";
    public static final String EXTRA_GROUP_ROLE = "extra_group_role";
    private static final DateTimeFormatter EVENT_API_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter[] DATE_TIME_INPUT_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    };
    private static final DateTimeFormatter[] DATE_INPUT_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    private GroupEventsRepository groupEventsRepository;
    private GroupMembershipRepository groupMembershipRepository;
    private GroupEventAdapter adapter;
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

        setContentView(R.layout.activity_group_events);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
        role = Role.from(getIntent().getStringExtra(EXTRA_GROUP_ROLE));
        if (groupId == null || groupName == null) {
            finish();
            return;
        }

        groupEventsRepository = new GroupEventsRepository();
        groupMembershipRepository = new GroupMembershipRepository();

        bindChrome();
        bindList();
        bindForm();
        loadMembershipAndEvents(sessionManager.getUserId());
    }

    private void bindChrome() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_group_events);
        toolbar.setTitle(groupName);

        Button backButton = findViewById(R.id.btn_back_from_group_events);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        TextView subtitle = findViewById(R.id.tv_group_events_subtitle);
        subtitle.setText(role == Role.ADMIN
                ? "Create and maintain your group calendar events."
                : "View upcoming and past events for this group.");
    }

    private void bindList() {
        ListView listView = findViewById(R.id.lv_group_events);
        adapter = new GroupEventAdapter(this, new GroupEventAdapter.Listener() {
            @Override
            public void onEdit(Event event) {
                showEditDialog(event);
            }

            @Override
            public void onDelete(Event event) {
                confirmDelete(event);
            }
        });
        adapter.setCurrentRole(role);
        listView.setAdapter(adapter);

        Button retryButton = findViewById(R.id.btn_retry_group_events);
        retryButton.setOnClickListener(v -> loadEvents());
    }

    private void bindForm() {
        View form = findViewById(R.id.layout_group_events_form);
        form.setVisibility(role == Role.ADMIN ? View.VISIBLE : View.GONE);

        Button createButton = findViewById(R.id.btn_create_group_event);
        createButton.setOnClickListener(v -> submitEvent());
    }

    private void loadMembershipAndEvents(String userId) {
        if (role != Role.ADMIN) {
            loadEvents();
            return;
        }

        groupMembershipRepository.getMembership(userId, groupId, new RepositoryCallback<GroupMembership>() {
            @Override
            public void onSuccess(GroupMembership result) {
                currentMembership = result;
                loadEvents();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(GroupEventsActivity.this, message, Toast.LENGTH_LONG).show();
                loadEvents();
            }
        });
    }

    private void loadEvents() {
        setLoadingState(true, null);
        groupEventsRepository.getGroupEvents(groupId, new RepositoryCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.submitEvents(result);
                setLoadingState(false, result == null || result.isEmpty() ? "No events have been created for this group yet." : null);
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                adapter.submitEvents(null);
                setLoadingState(false, message);
            }
        });
    }

    private void submitEvent() {
        EditText titleInput = findViewById(R.id.et_group_event_title);
        EditText descriptionInput = findViewById(R.id.et_group_event_description);
        EditText dateTimeInput = findViewById(R.id.et_group_event_datetime);
        Button createButton = findViewById(R.id.btn_create_group_event);

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String dateTime = normalizeEventDateTime(dateTimeInput.getText().toString().trim());

        if (TextUtils.isEmpty(title)) {
            titleInput.setError("Enter an event title.");
            titleInput.requestFocus();
            return;
        }
        if (dateTime == null) {
            dateTimeInput.setError("Use yyyy-MM-dd HH:mm, for example 2026-05-05 14:00.");
            dateTimeInput.requestFocus();
            return;
        }
        if (currentMembership == null || currentMembership.getMembershipId() == null || currentMembership.getMembershipId().isEmpty()) {
            Toast.makeText(this, "Your admin membership could not be resolved yet.", Toast.LENGTH_LONG).show();
            return;
        }

        createButton.setEnabled(false);
        createButton.setText("Creating...");
        groupEventsRepository.createEvent(
                groupId,
                title,
                description,
                dateTime,
                currentMembership.getMembershipId(),
                new RepositoryCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        titleInput.setText("");
                        descriptionInput.setText("");
                        dateTimeInput.setText("");
                        createButton.setEnabled(true);
                        createButton.setText("Create Group Event");
                        Toast.makeText(GroupEventsActivity.this, "Group event created.", Toast.LENGTH_SHORT).show();
                        loadEvents();
                    }

                    @Override
                    public void onError(String message) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        createButton.setEnabled(true);
                        createButton.setText("Create Group Event");
                        Toast.makeText(GroupEventsActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void showEditDialog(Event event) {
        EditText titleInput = new EditText(this);
        titleInput.setHint("Event title");
        titleInput.setText(event.getEventName() == null ? "" : event.getEventName());
        styleDialogInput(titleInput);

        EditText descriptionInput = new EditText(this);
        descriptionInput.setHint("Description");
        descriptionInput.setText(event.getEventDescription() == null ? "" : event.getEventDescription());
        styleDialogInput(descriptionInput);

        EditText dateTimeInput = new EditText(this);
        dateTimeInput.setHint("Date and time");
        dateTimeInput.setText(event.getEventDateTime() == null ? "" : event.getEventDateTime());
        styleDialogInput(dateTimeInput);

        // Lightweight custom stack for quick editing.
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding / 2);
        layout.addView(titleInput);
        layout.addView(descriptionInput);
        layout.addView(dateTimeInput);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Edit Event")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> updateEvent(event, titleInput, descriptionInput, dateTimeInput))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void styleDialogInput(EditText input) {
        input.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        input.setHintTextColor(ContextCompat.getColor(this, R.color.on_surface_variant));
    }

    private void updateEvent(Event event, EditText titleInput, EditText descriptionInput, EditText dateTimeInput) {
        String eventId = event.getEventId();
        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(this, "This event cannot be edited right now.", Toast.LENGTH_LONG).show();
            return;
        }

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String dateTime = normalizeEventDateTime(dateTimeInput.getText().toString().trim());

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Title is required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (dateTime == null) {
            Toast.makeText(this, "Use yyyy-MM-dd HH:mm for the event date/time.", Toast.LENGTH_LONG).show();
            return;
        }

        groupEventsRepository.updateEvent(eventId, title, description, dateTime, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(GroupEventsActivity.this, "Event updated.", Toast.LENGTH_SHORT).show();
                loadEvents();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(GroupEventsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String normalizeEventDateTime(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }

        String trimmed = rawValue.trim();
        for (DateTimeFormatter formatter : DATE_TIME_INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(trimmed, formatter).format(EVENT_API_FORMAT);
            } catch (DateTimeParseException ignored) {
            }
        }

        for (DateTimeFormatter formatter : DATE_INPUT_FORMATS) {
            try {
                return LocalDate.parse(trimmed, formatter).atStartOfDay().format(EVENT_API_FORMAT);
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private void confirmDelete(Event event) {
        String eventId = event.getEventId();
        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(this, "This event cannot be deleted right now.", Toast.LENGTH_LONG).show();
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(event.getEventName() == null ? "Delete event" : event.getEventName())
                .setMessage("Delete this event from the group calendar?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent(eventId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEvent(String eventId) {
        groupEventsRepository.deleteEvent(eventId, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(GroupEventsActivity.this, "Event deleted.", Toast.LENGTH_SHORT).show();
                loadEvents();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(GroupEventsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean loading, String message) {
        View progressView = findViewById(R.id.progress_group_events);
        ListView listView = findViewById(R.id.lv_group_events);
        TextView messageView = findViewById(R.id.tv_group_events_message);
        Button retryButton = findViewById(R.id.btn_retry_group_events);

        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);
        listView.setVisibility(!loading && message == null ? View.VISIBLE : View.GONE);
        messageView.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        retryButton.setVisibility(!loading && message != null ? View.VISIBLE : View.GONE);
        messageView.setText(message == null ? "" : message);
    }
}
