package com.example.manageit.fragments.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.manageit.R;
import com.example.manageit.activities.GroupAnnouncementsActivity;
import com.example.manageit.activities.GroupBudgetActivity;
import com.example.manageit.activities.GroupEventsActivity;
import com.example.manageit.activities.GroupTasksActivity;
import com.example.manageit.apis.marketaux.MarketauxNewsRepository;
import com.example.manageit.apis.marketaux.models.MarketauxArticle;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Announcement;
import com.example.manageit.models.Event;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Role;
import com.example.manageit.models.Task;
import com.example.manageit.models.User;
import com.example.manageit.repository.GroupAnnouncementsRepository;
import com.example.manageit.repository.GroupEventsRepository;
import com.example.manageit.repository.GroupMembershipRepository;
import com.example.manageit.repository.GroupTasksRepository;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.utils.GreetingUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dashboard for a user membership inside a selected student group.
 */
public class UserDashboardFragment extends Fragment {

    private static final String ARG_GROUP_ID = "arg_group_id";
    private static final String ARG_GROUP_NAME = "arg_group_name";
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter[] DATE_TIME_INPUT_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    };
    private static final DateTimeFormatter[] DATE_INPUT_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    private final MarketauxNewsRepository marketauxNewsRepository = new MarketauxNewsRepository();
    private final GroupMembershipRepository groupMembershipRepository = new GroupMembershipRepository();
    private final GroupTasksRepository groupTasksRepository = new GroupTasksRepository();
    private final GroupEventsRepository groupEventsRepository = new GroupEventsRepository();
    private final GroupAnnouncementsRepository groupAnnouncementsRepository = new GroupAnnouncementsRepository();
    private boolean newsLoaded;
    private @Nullable View rootView;
    private String currentGroupId = "";
    private String currentUserId = "";
    private boolean skipNextResumeRefresh;

    public UserDashboardFragment() {
        super(R.layout.fragment_dashboard_user);
    }

    public static UserDashboardFragment newInstance(String groupId, String groupName) {
        UserDashboardFragment fragment = new UserDashboardFragment();
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

        Bundle args = getArguments();
        String groupId = args != null ? args.getString(ARG_GROUP_ID, "") : "";
        String groupName = args != null ? args.getString(ARG_GROUP_NAME, "Student Group") : "Student Group";
        currentGroupId = groupId;

        SessionManager sessionManager = new SessionManager(requireContext());
        currentUserId = sessionManager.getUserId();
        User currentUser = sessionManager.getCurrentUser();

        ((TextView) view.findViewById(R.id.tv_dashboard_group_label)).setText(groupName);
        ((TextView) view.findViewById(R.id.tv_dashboard_greeting)).setText(GreetingUtils.getGreetingForCurrentTime());
        ((TextView) view.findViewById(R.id.tv_dashboard_name)).setText(GreetingUtils.getDisplayFirstName(currentUser));
        ((TextView) view.findViewById(R.id.tv_dashboard_membership_role)).setText("Group role: User");
        ((TextView) view.findViewById(R.id.tv_dashboard_avatar_initial)).setText(GreetingUtils.getInitials(currentUser));
        ((TextView) view.findViewById(R.id.tv_news_description)).setText(
                "Relevant finance headlines for " + groupName + "."
        );

        bindTabs(view, groupName);
        bindRetry(view, groupName);
        bindOverviewActions(view, groupId, groupName);
        showOverviewTab(view);
        loadDashboardOverview(view, groupId, currentUserId);
        skipNextResumeRefresh = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (skipNextResumeRefresh) {
            skipNextResumeRefresh = false;
            return;
        }
        if (rootView != null && !currentGroupId.trim().isEmpty()) {
            loadDashboardOverview(rootView, currentGroupId, currentUserId);
        }
    }

    private void bindOverviewActions(View root, String groupId, String groupName) {
        Button tasksButton = root.findViewById(R.id.btn_open_user_group_tasks);
        tasksButton.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(requireContext(), GroupTasksActivity.class);
            intent.putExtra(GroupTasksActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(GroupTasksActivity.EXTRA_GROUP_NAME, groupName);
            intent.putExtra(GroupTasksActivity.EXTRA_GROUP_ROLE, Role.USER.name());
            startActivity(intent);
        });

        Button announcementsButton = root.findViewById(R.id.btn_open_user_group_announcements);
        announcementsButton.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(requireContext(), GroupAnnouncementsActivity.class);
            intent.putExtra(GroupAnnouncementsActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(GroupAnnouncementsActivity.EXTRA_GROUP_NAME, groupName);
            intent.putExtra(GroupAnnouncementsActivity.EXTRA_GROUP_ROLE, Role.USER.name());
            startActivity(intent);
        });

        Button eventsButton = root.findViewById(R.id.btn_open_user_group_events);
        eventsButton.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(requireContext(), GroupEventsActivity.class);
            intent.putExtra(GroupEventsActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(GroupEventsActivity.EXTRA_GROUP_NAME, groupName);
            intent.putExtra(GroupEventsActivity.EXTRA_GROUP_ROLE, Role.USER.name());
            startActivity(intent);
        });

        Button budgetButton = root.findViewById(R.id.btn_open_user_group_budget);
        budgetButton.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(requireContext(), GroupBudgetActivity.class);
            intent.putExtra(GroupBudgetActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(GroupBudgetActivity.EXTRA_GROUP_NAME, groupName);
            intent.putExtra(GroupBudgetActivity.EXTRA_GROUP_ROLE, Role.USER.name());
            startActivity(intent);
        });

        Button refreshButton = root.findViewById(R.id.btn_refresh_user_dashboard);
        refreshButton.setOnClickListener(v -> loadDashboardOverview(root, groupId, currentUserId));
    }

    private void loadDashboardOverview(View root, String groupId, String userId) {
        bindOverviewDefaults(root);
        loadMembershipAndTasks(root, groupId, userId);
        loadEventOverview(root, groupId);
        loadAnnouncementOverview(root, groupId);
    }

    private void bindOverviewDefaults(View root) {
        ((TextView) root.findViewById(R.id.tv_user_dashboard_overview_status))
                .setText("Refreshing your group overview...");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_next_task_title)).setText("Loading task...");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_next_task_meta)).setText("Due date unavailable");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_next_event_title)).setText("Loading event...");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_next_event_meta)).setText("Event date unavailable");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_pending_count)).setText("--");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_in_progress_count)).setText("--");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_due_soon_count)).setText("--");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_latest_announcement_title)).setText("Loading announcement...");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_latest_announcement_body)).setText("Checking the latest group update.");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_latest_announcement_meta)).setText("Published date unavailable");
    }

    private void loadMembershipAndTasks(View root, String groupId, String userId) {
        groupMembershipRepository.getMembership(userId, groupId, new RepositoryCallback<GroupMembership>() {
            @Override
            public void onSuccess(GroupMembership result) {
                if (!isAdded()) {
                    return;
                }

                if (result == null || isBlank(result.getMembershipId())) {
                    bindTaskOverviewUnavailable(root, "Your membership could not be resolved for this group.");
                    return;
                }

                loadTaskOverview(root, groupId, result.getMembershipId());
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                bindTaskOverviewUnavailable(root, message);
            }
        });
    }

    private void loadTaskOverview(View root, String groupId, String membershipId) {
        groupTasksRepository.getGroupTasks(groupId, new RepositoryCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> result) {
                if (!isAdded()) {
                    return;
                }

                int pending = 0;
                int inProgress = 0;
                int dueSoon = 0;
                Task nextTask = null;
                LocalDateTime nextDueAt = null;
                Task firstOpenTaskWithoutDate = null;
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime dueSoonLimit = now.plusDays(3);

                if (result != null) {
                    for (Task task : result) {
                        if (task == null || !sameId(membershipId, task.getAssignedToMembershipId())) {
                            continue;
                        }

                        String status = task.getStatus() == null ? "" : task.getStatus();
                        if ("pending".equalsIgnoreCase(status)) {
                            pending++;
                        } else if ("in_progress".equalsIgnoreCase(status)) {
                            inProgress++;
                        }

                        if (isClosedTask(task)) {
                            continue;
                        }

                        LocalDateTime dueAt = parseDateTime(task.getDueDate());
                        if (dueAt != null) {
                            if (!dueAt.isAfter(dueSoonLimit)) {
                                dueSoon++;
                            }
                            if (nextDueAt == null || dueAt.isBefore(nextDueAt)) {
                                nextTask = task;
                                nextDueAt = dueAt;
                            }
                        } else if (firstOpenTaskWithoutDate == null) {
                            firstOpenTaskWithoutDate = task;
                        }
                    }
                }

                ((TextView) root.findViewById(R.id.tv_user_dashboard_pending_count)).setText(String.valueOf(pending));
                ((TextView) root.findViewById(R.id.tv_user_dashboard_in_progress_count)).setText(String.valueOf(inProgress));
                ((TextView) root.findViewById(R.id.tv_user_dashboard_due_soon_count)).setText(String.valueOf(dueSoon));

                if (nextTask == null) {
                    nextTask = firstOpenTaskWithoutDate;
                }
                bindNextTask(root, nextTask, nextDueAt);
                ((TextView) root.findViewById(R.id.tv_user_dashboard_overview_status))
                        .setText("Live snapshot for your selected group.");
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                bindTaskOverviewUnavailable(root, message);
            }
        });
    }

    private void bindTaskOverviewUnavailable(View root, String message) {
        ((TextView) root.findViewById(R.id.tv_user_dashboard_pending_count)).setText("N/A");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_in_progress_count)).setText("N/A");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_due_soon_count)).setText("N/A");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_next_task_title)).setText("Task overview unavailable");
        ((TextView) root.findViewById(R.id.tv_user_dashboard_next_task_meta)).setText(message);
        ((TextView) root.findViewById(R.id.tv_user_dashboard_overview_status))
                .setText("Some dashboard data could not be refreshed.");
    }

    private void bindNextTask(View root, @Nullable Task task, @Nullable LocalDateTime parsedDueAt) {
        TextView titleView = root.findViewById(R.id.tv_user_dashboard_next_task_title);
        TextView metaView = root.findViewById(R.id.tv_user_dashboard_next_task_meta);

        if (task == null) {
            titleView.setText("No open tasks assigned to you");
            metaView.setText("Open Tasks to review the full group task list.");
            return;
        }

        titleView.setText(valueOrFallback(task.getTitle(), "Untitled task"));
        String status = humanizeStatus(task.getStatus());
        LocalDateTime dueAt = parsedDueAt == null ? parseDateTime(task.getDueDate()) : parsedDueAt;
        if (dueAt == null) {
            metaView.setText(status + " • Due date unavailable");
            return;
        }
        metaView.setText(status + " • " + formatDueLabel(dueAt));
    }

    private void loadEventOverview(View root, String groupId) {
        groupEventsRepository.getGroupEvents(groupId, new RepositoryCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> result) {
                if (!isAdded()) {
                    return;
                }

                Event nextEvent = null;
                LocalDateTime nextEventAt = null;
                Event firstEventWithoutDate = null;
                LocalDateTime now = LocalDateTime.now();

                if (result != null) {
                    for (Event event : result) {
                        if (event == null) {
                            continue;
                        }
                        LocalDateTime eventAt = parseDateTime(event.getEventDateTime());
                        if (eventAt == null) {
                            if (firstEventWithoutDate == null) {
                                firstEventWithoutDate = event;
                            }
                            continue;
                        }
                        if (eventAt.isBefore(now)) {
                            continue;
                        }
                        if (nextEventAt == null || eventAt.isBefore(nextEventAt)) {
                            nextEvent = event;
                            nextEventAt = eventAt;
                        }
                    }
                }

                if (nextEvent == null) {
                    nextEvent = firstEventWithoutDate;
                }
                bindNextEvent(root, nextEvent, nextEventAt);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                ((TextView) root.findViewById(R.id.tv_user_dashboard_next_event_title)).setText("Event overview unavailable");
                ((TextView) root.findViewById(R.id.tv_user_dashboard_next_event_meta)).setText(message);
            }
        });
    }

    private void bindNextEvent(View root, @Nullable Event event, @Nullable LocalDateTime parsedEventAt) {
        TextView titleView = root.findViewById(R.id.tv_user_dashboard_next_event_title);
        TextView metaView = root.findViewById(R.id.tv_user_dashboard_next_event_meta);

        if (event == null) {
            titleView.setText("No upcoming events");
            metaView.setText("Open Events to review the full group schedule.");
            return;
        }

        titleView.setText(valueOrFallback(event.getEventName(), "Untitled event"));
        LocalDateTime eventAt = parsedEventAt == null ? parseDateTime(event.getEventDateTime()) : parsedEventAt;
        if (eventAt == null) {
            metaView.setText(valueOrFallback(event.getEventDateTime(), "Event date unavailable"));
            return;
        }
        metaView.setText("Scheduled " + eventAt.format(DISPLAY_DATE_TIME));
    }

    private void loadAnnouncementOverview(View root, String groupId) {
        groupAnnouncementsRepository.getGroupAnnouncements(groupId, new RepositoryCallback<List<Announcement>>() {
            @Override
            public void onSuccess(List<Announcement> result) {
                if (!isAdded()) {
                    return;
                }

                Announcement latest = null;
                LocalDateTime latestAt = null;
                if (result != null) {
                    for (Announcement announcement : result) {
                        if (announcement == null) {
                            continue;
                        }

                        LocalDateTime publishedAt = parseDateTime(announcement.getPublishedAt());
                        if (latest == null) {
                            latest = announcement;
                            latestAt = publishedAt;
                            continue;
                        }
                        if (publishedAt != null && (latestAt == null || publishedAt.isAfter(latestAt))) {
                            latest = announcement;
                            latestAt = publishedAt;
                        }
                    }
                }

                bindLatestAnnouncement(root, latest, latestAt);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                ((TextView) root.findViewById(R.id.tv_user_dashboard_latest_announcement_title))
                        .setText("Announcements unavailable");
                ((TextView) root.findViewById(R.id.tv_user_dashboard_latest_announcement_body)).setText(message);
                ((TextView) root.findViewById(R.id.tv_user_dashboard_latest_announcement_meta)).setText("");
            }
        });
    }

    private void bindLatestAnnouncement(
            View root,
            @Nullable Announcement announcement,
            @Nullable LocalDateTime publishedAt
    ) {
        TextView titleView = root.findViewById(R.id.tv_user_dashboard_latest_announcement_title);
        TextView bodyView = root.findViewById(R.id.tv_user_dashboard_latest_announcement_body);
        TextView metaView = root.findViewById(R.id.tv_user_dashboard_latest_announcement_meta);

        if (announcement == null) {
            titleView.setText("No announcements yet");
            bodyView.setText("Admins have not published an announcement for this group.");
            metaView.setText("");
            return;
        }

        titleView.setText(valueOrFallback(announcement.getTitle(), "Untitled announcement"));
        bodyView.setText(truncate(valueOrFallback(announcement.getBody(), "No message provided."), 150));
        if (publishedAt == null) {
            metaView.setText(valueOrFallback(announcement.getPublishedAt(), "Published date unavailable"));
            return;
        }
        metaView.setText("Published " + publishedAt.format(DISPLAY_DATE_TIME));
    }

    private void bindTabs(View root, String groupName) {
        Button overviewTab = root.findViewById(R.id.btn_tab_overview);
        Button newsTab = root.findViewById(R.id.btn_tab_news);

        overviewTab.setOnClickListener(v -> showOverviewTab(root));
        newsTab.setOnClickListener(v -> {
            showNewsTab(root);
            if (!newsLoaded) {
                loadNews(root, groupName);
            }
        });
    }

    private void bindRetry(View root, String groupName) {
        Button retryButton = root.findViewById(R.id.btn_retry_ft_news);
        retryButton.setOnClickListener(v -> loadNews(root, groupName));
    }

    private void showOverviewTab(View root) {
        Button overviewTab = root.findViewById(R.id.btn_tab_overview);
        Button newsTab = root.findViewById(R.id.btn_tab_news);
        root.findViewById(R.id.layout_dashboard_overview).setVisibility(View.VISIBLE);
        root.findViewById(R.id.layout_dashboard_news).setVisibility(View.GONE);
        applySelectedTabStyle(overviewTab, true);
        applySelectedTabStyle(newsTab, false);
    }

    private void showNewsTab(View root) {
        Button overviewTab = root.findViewById(R.id.btn_tab_overview);
        Button newsTab = root.findViewById(R.id.btn_tab_news);
        root.findViewById(R.id.layout_dashboard_overview).setVisibility(View.GONE);
        root.findViewById(R.id.layout_dashboard_news).setVisibility(View.VISIBLE);
        applySelectedTabStyle(overviewTab, false);
        applySelectedTabStyle(newsTab, true);
    }

    private void applySelectedTabStyle(Button button, boolean selected) {
        button.setBackgroundResource(selected ? R.drawable.bg_button_primary : R.drawable.bg_button_secondary);
        button.setTextColor(ContextCompat.getColor(
                requireContext(),
                selected ? R.color.on_primary : R.color.on_surface
        ));
    }

    private void loadNews(View root, String groupName) {
        ProgressBar progressBar = root.findViewById(R.id.progress_ft_news);
        TextView messageView = root.findViewById(R.id.tv_ft_news_message);
        Button retryButton = root.findViewById(R.id.btn_retry_ft_news);
        LinearLayout articlesContainer = root.findViewById(R.id.layout_ft_news_articles);

        progressBar.setVisibility(View.VISIBLE);
        messageView.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        articlesContainer.removeAllViews();

        marketauxNewsRepository.getNewsForGroup(groupName, new RepositoryCallback<List<MarketauxArticle>>() {
            @Override
            public void onSuccess(List<MarketauxArticle> result) {
                if (!isAdded()) {
                    return;
                }

                newsLoaded = true;
                progressBar.setVisibility(View.GONE);
                if (result == null || result.isEmpty()) {
                    messageView.setText("No finance news articles were returned for this group right now.");
                    messageView.setVisibility(View.VISIBLE);
                    retryButton.setVisibility(View.VISIBLE);
                    return;
                }

                LayoutInflater inflater = LayoutInflater.from(requireContext());
                for (MarketauxArticle article : result) {
                    View articleView = inflater.inflate(R.layout.item_ft_news_article, articlesContainer, false);
                    ((TextView) articleView.findViewById(R.id.tv_ft_article_source)).setText(article.getSource());
                    ((TextView) articleView.findViewById(R.id.tv_ft_article_title)).setText(article.getTitle());
                    ((TextView) articleView.findViewById(R.id.tv_ft_article_published)).setText(
                            formatPublishedLabel(article.getPublishedAt())
                    );
                    articlesContainer.addView(articleView);
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }

                newsLoaded = false;
                progressBar.setVisibility(View.GONE);
                messageView.setText(message);
                messageView.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private String formatPublishedLabel(String rawTimestamp) {
        if (rawTimestamp == null || rawTimestamp.trim().isEmpty()) {
            return "Published date unavailable";
        }

        try {
            OffsetDateTime dateTime = OffsetDateTime.parse(rawTimestamp);
            return "Published " + dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        } catch (DateTimeParseException ignored) {
            return "Published " + rawTimestamp;
        }
    }

    private LocalDateTime parseDateTime(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }

        String trimmed = rawValue.trim();
        for (DateTimeFormatter formatter : DATE_TIME_INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        for (DateTimeFormatter formatter : DATE_INPUT_FORMATS) {
            try {
                return LocalDate.parse(trimmed, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private String formatDueLabel(LocalDateTime dueAt) {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = dueAt.toLocalDate();
        if (dueDate.isBefore(today)) {
            return "Overdue since " + dueAt.format(DISPLAY_DATE_TIME);
        }
        if (dueDate.isEqual(today)) {
            return "Due today at " + dueAt.format(DISPLAY_TIME);
        }
        if (dueDate.isEqual(today.plusDays(1))) {
            return "Due tomorrow at " + dueAt.format(DISPLAY_TIME);
        }
        return "Due " + dueAt.format(DISPLAY_DATE_TIME);
    }

    private String humanizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Status unavailable";
        }

        String normalized = status.trim().toLowerCase();
        switch (normalized) {
            case "in_progress":
                return "In progress";
            case "completed":
                return "Completed";
            case "cancelled":
                return "Cancelled";
            case "pending":
                return "Pending";
            default:
                return normalized.replace("_", " ");
        }
    }

    private boolean isClosedTask(Task task) {
        if (task == null) {
            return true;
        }
        String status = task.getStatus();
        return task.isCompleted() || "cancelled".equalsIgnoreCase(status);
    }

    private boolean sameId(String first, String second) {
        return first != null
                && second != null
                && first.trim().equals(second.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String valueOrFallback(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, Math.max(0, maxLength - 3)).trim() + "...";
    }
}
