package com.example.manageit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.manageit.R;
import com.example.manageit.models.Role;
import com.example.manageit.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Renders group tasks for admin and user screens.
 */
public class GroupTaskAdapter extends BaseAdapter {

    public interface Listener {
        void onUpdateStatus(Task task);
    }

    private final LayoutInflater inflater;
    private final Listener listener;
    private final List<Task> tasks = new ArrayList<>();
    private Map<String, String> memberNamesByMembershipId;
    private String currentMembershipId;
    private Role currentRole = Role.USER;

    public GroupTaskAdapter(Context context, Listener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void submitTasks(List<Task> newTasks) {
        tasks.clear();
        if (newTasks != null) {
            tasks.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    public void setMemberNamesByMembershipId(Map<String, String> memberNamesByMembershipId) {
        this.memberNamesByMembershipId = memberNamesByMembershipId;
        notifyDataSetChanged();
    }

    public void setCurrentMembershipId(String currentMembershipId) {
        this.currentMembershipId = currentMembershipId;
        notifyDataSetChanged();
    }

    public void setCurrentRole(Role currentRole) {
        this.currentRole = currentRole == null ? Role.USER : currentRole;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_group_task, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Task task = getItem(position);
        holder.titleView.setText(task.getTitle());
        String description = task.getDescription() == null ? "" : task.getDescription().trim();
        holder.descriptionView.setText(description);
        holder.descriptionView.setVisibility(description.isEmpty() ? View.GONE : View.VISIBLE);
        holder.statusView.setText(formatStatus(task.getStatus()));
        holder.dueDateView.setText(task.getDueDate() == null || task.getDueDate().isEmpty()
                ? "Due date not set"
                : "Due: " + task.getDueDate());
        boolean isAssignee = currentMembershipId != null
                && currentMembershipId.equals(task.getAssignedToMembershipId());
        holder.assignedByView.setText(buildAssignedByLabel(task));
        holder.assignedToView.setText(buildAssignedToLabel(task, isAssignee));
        boolean canUpdateStatus = isAssignee || currentRole == Role.ADMIN;
        if (canUpdateStatus) {
            holder.updateStatusButton.setVisibility(View.VISIBLE);
            holder.updateStatusButton.setText(getUpdateButtonLabel(task, isAssignee));
            holder.updateStatusButton.setOnClickListener(v -> listener.onUpdateStatus(task));
        } else {
            holder.updateStatusButton.setVisibility(View.GONE);
            holder.updateStatusButton.setOnClickListener(null);
        }

        return convertView;
    }

    private String resolveMemberName(String membershipId) {
        if (membershipId == null || membershipId.isEmpty()) {
            return "Unassigned";
        }
        if (memberNamesByMembershipId == null) {
            return membershipId;
        }
        String name = memberNamesByMembershipId.get(membershipId);
        return name == null || name.isEmpty() ? membershipId : name;
    }

    private String formatStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Pending";
        }

        String normalized = status.trim().replace('_', ' ');
        String[] parts = normalized.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1).toLowerCase());
            }
        }
        return builder.toString();
    }

    private String getUpdateButtonLabel(Task task, boolean isAssignee) {
        if (currentRole == Role.ADMIN && !isAssignee) {
            return "Update Task Status";
        }
        if (task.isCompleted()) {
            return "Reopen Or Change Status";
        }
        return "Update My Task Status";
    }

    private String buildAssignedByLabel(Task task) {
        return "Assigned by: " + resolveMemberName(task.getAssignedByMembershipId());
    }

    private String buildAssignedToLabel(Task task, boolean isAssignee) {
        if (currentRole == Role.USER) {
            if (isAssignee) {
                return "Assigned to: You";
            }
            return "Assigned to: " + resolveMemberName(task.getAssignedToMembershipId());
        }
        return "Assigned to: " + resolveMemberName(task.getAssignedToMembershipId());
    }

    private static final class ViewHolder {
        private final TextView titleView;
        private final TextView descriptionView;
        private final TextView statusView;
        private final TextView dueDateView;
        private final TextView assignedToView;
        private final TextView assignedByView;
        private final Button updateStatusButton;

        private ViewHolder(View itemView) {
            this.titleView = itemView.findViewById(R.id.tv_group_task_title);
            this.descriptionView = itemView.findViewById(R.id.tv_group_task_description);
            this.statusView = itemView.findViewById(R.id.tv_group_task_status);
            this.dueDateView = itemView.findViewById(R.id.tv_group_task_due_date);
            this.assignedToView = itemView.findViewById(R.id.tv_group_task_assigned_to);
            this.assignedByView = itemView.findViewById(R.id.tv_group_task_assigned_by);
            this.updateStatusButton = itemView.findViewById(R.id.btn_update_group_task_status);
        }
    }
}
