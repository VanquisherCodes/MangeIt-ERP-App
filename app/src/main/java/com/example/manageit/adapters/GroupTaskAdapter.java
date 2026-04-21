package com.example.manageit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.manageit.R;
import com.example.manageit.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Renders group tasks for admin and user screens.
 */
public class GroupTaskAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<Task> tasks = new ArrayList<>();
    private Map<String, String> memberNamesByMembershipId;

    public GroupTaskAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
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
        holder.descriptionView.setText(task.getDescription() == null ? "" : task.getDescription());
        holder.statusView.setText(task.getStatus() == null ? "pending" : task.getStatus());
        holder.dueDateView.setText(task.getDueDate() == null || task.getDueDate().isEmpty()
                ? "Due date not set"
                : "Due: " + task.getDueDate());
        holder.assignedToView.setText("Assigned to: " + resolveMemberName(task.getAssignedToMembershipId()));

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

    private static final class ViewHolder {
        private final TextView titleView;
        private final TextView descriptionView;
        private final TextView statusView;
        private final TextView dueDateView;
        private final TextView assignedToView;

        private ViewHolder(View itemView) {
            this.titleView = itemView.findViewById(R.id.tv_group_task_title);
            this.descriptionView = itemView.findViewById(R.id.tv_group_task_description);
            this.statusView = itemView.findViewById(R.id.tv_group_task_status);
            this.dueDateView = itemView.findViewById(R.id.tv_group_task_due_date);
            this.assignedToView = itemView.findViewById(R.id.tv_group_task_assigned_to);
        }
    }
}
