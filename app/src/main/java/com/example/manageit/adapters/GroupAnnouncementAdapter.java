package com.example.manageit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.manageit.R;
import com.example.manageit.models.Announcement;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders group announcements for admin and user screens.
 */
public class GroupAnnouncementAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<Announcement> announcements = new ArrayList<>();

    public GroupAnnouncementAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void submitAnnouncements(List<Announcement> newAnnouncements) {
        announcements.clear();
        if (newAnnouncements != null) {
            announcements.addAll(newAnnouncements);
        }
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

        return convertView;
    }

    private static final class ViewHolder {
        private final TextView titleView;
        private final TextView messageView;
        private final TextView publishedAtView;

        private ViewHolder(View itemView) {
            this.titleView = itemView.findViewById(R.id.tv_group_announcement_title);
            this.messageView = itemView.findViewById(R.id.tv_group_announcement_message);
            this.publishedAtView = itemView.findViewById(R.id.tv_group_announcement_published_at);
        }
    }
}
