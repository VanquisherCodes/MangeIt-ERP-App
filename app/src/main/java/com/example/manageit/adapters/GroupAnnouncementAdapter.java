package com.example.manageit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.manageit.R;
import com.example.manageit.models.Announcement;
import com.example.manageit.models.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders group announcements for admin and user screens.
 */
public class GroupAnnouncementAdapter extends BaseAdapter {

    public interface Listener {
        void onEdit(Announcement announcement);

        void onDelete(Announcement announcement);
    }

    private final LayoutInflater inflater;
    private final Listener listener;
    private final List<Announcement> announcements = new ArrayList<>();
    private Role currentRole = Role.USER;

    public GroupAnnouncementAdapter(Context context, Listener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void submitAnnouncements(List<Announcement> newAnnouncements) {
        announcements.clear();
        if (newAnnouncements != null) {
            announcements.addAll(newAnnouncements);
        }
        notifyDataSetChanged();
    }

    public void setCurrentRole(Role role) {
        this.currentRole = role == null ? Role.USER : role;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return announcements.size();
    }

    @Override
    public Announcement getItem(int position) {
        return announcements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_group_announcement, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Announcement announcement = getItem(position);
        holder.titleView.setText(announcement.getTitle());
        holder.messageView.setText(announcement.getBody() == null ? "" : announcement.getBody());
        holder.publishedAtView.setText(
                announcement.getPublishedAt() == null || announcement.getPublishedAt().isEmpty()
                        ? "Published date unavailable"
                        : "Published: " + announcement.getPublishedAt()
        );

        boolean isAdmin = currentRole == Role.ADMIN;
        holder.editButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        holder.editButton.setOnClickListener(isAdmin ? v -> listener.onEdit(announcement) : null);
        holder.deleteButton.setOnClickListener(isAdmin ? v -> listener.onDelete(announcement) : null);
        return convertView;
    }

    private static final class ViewHolder {
        private final TextView titleView;
        private final TextView messageView;
        private final TextView publishedAtView;
        private final Button editButton;
        private final Button deleteButton;

        private ViewHolder(View itemView) {
            this.titleView = itemView.findViewById(R.id.tv_group_announcement_title);
            this.messageView = itemView.findViewById(R.id.tv_group_announcement_message);
            this.publishedAtView = itemView.findViewById(R.id.tv_group_announcement_published_at);
            this.editButton = itemView.findViewById(R.id.btn_edit_group_announcement);
            this.deleteButton = itemView.findViewById(R.id.btn_delete_group_announcement);
        }
    }
}
