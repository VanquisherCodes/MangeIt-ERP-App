package com.example.manageit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.manageit.R;
import com.example.manageit.models.Event;
import com.example.manageit.models.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders group events for admin and user dashboards.
 */
public class GroupEventAdapter extends BaseAdapter {

    public interface Listener {
        void onEdit(Event event);

        void onDelete(Event event);
    }

    private final LayoutInflater inflater;
    private final Listener listener;
    private final List<Event> events = new ArrayList<>();
    private Role currentRole = Role.USER;

    public GroupEventAdapter(Context context, Listener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void submitEvents(List<Event> newEvents) {
        events.clear();
        if (newEvents != null) {
            events.addAll(newEvents);
        }
        notifyDataSetChanged();
    }

    public void setCurrentRole(Role role) {
        this.currentRole = role == null ? Role.USER : role;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_group_event, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Event event = getItem(position);
        holder.titleView.setText(valueOrFallback(event.getEventName(), "Untitled event"));
        holder.dateTimeView.setText(buildDateTimeLabel(event.getEventDateTime()));

        String description = event.getEventDescription() == null ? "" : event.getEventDescription().trim();
        holder.descriptionView.setText(description);
        holder.descriptionView.setVisibility(description.isEmpty() ? View.GONE : View.VISIBLE);

        boolean isAdmin = currentRole == Role.ADMIN;
        holder.editButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        holder.editButton.setOnClickListener(isAdmin ? v -> listener.onEdit(event) : null);
        holder.deleteButton.setOnClickListener(isAdmin ? v -> listener.onDelete(event) : null);
        return convertView;
    }

    private String valueOrFallback(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }

    private String buildDateTimeLabel(String dateTime) {
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return "Date/time not set";
        }
        return "When: " + dateTime.trim();
    }

    private static final class ViewHolder {
        private final TextView titleView;
        private final TextView dateTimeView;
        private final TextView descriptionView;
        private final Button editButton;
        private final Button deleteButton;

        private ViewHolder(View itemView) {
            this.titleView = itemView.findViewById(R.id.tv_group_event_title);
            this.dateTimeView = itemView.findViewById(R.id.tv_group_event_datetime);
            this.descriptionView = itemView.findViewById(R.id.tv_group_event_description);
            this.editButton = itemView.findViewById(R.id.btn_edit_group_event);
            this.deleteButton = itemView.findViewById(R.id.btn_delete_group_event);
        }
    }
}
