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
import com.example.manageit.activities.GroupTasksActivity;
import com.example.manageit.apis.marketaux.MarketauxNewsRepository;
import com.example.manageit.apis.marketaux.models.MarketauxArticle;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Role;
import com.example.manageit.models.User;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.utils.GreetingUtils;

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

    private final MarketauxNewsRepository marketauxNewsRepository = new MarketauxNewsRepository();
    private boolean newsLoaded;

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

        Bundle args = getArguments();
        String groupId = args != null ? args.getString(ARG_GROUP_ID, "") : "";
        String groupName = args != null ? args.getString(ARG_GROUP_NAME, "Student Group") : "Student Group";
        User currentUser = new SessionManager(requireContext()).getCurrentUser();

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
}
