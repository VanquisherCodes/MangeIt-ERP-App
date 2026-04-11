package com.example.manageit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manageit.R;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Role;
import com.example.manageit.models.StudentGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Renders the group list and its async membership states.
 */
public class GroupListAdapter extends BaseAdapter {

    public interface Listener {
        void onJoinAsUser(StudentGroup group);

        void onAdminAccessInfo(StudentGroup group);
    }

    private final LayoutInflater inflater;
    private final Listener listener;
    private final List<StudentGroup> groups = new ArrayList<>();
    private final Map<String, GroupMembership> membershipsByGroupId = new HashMap<>();
    private final Set<String> loadingGroupIds = new HashSet<>();
    private final Set<String> resolvedGroupIds = new HashSet<>();
    private final Set<String> errorGroupIds = new HashSet<>();
    private final Set<String> joiningGroupIds = new HashSet<>();

    public GroupListAdapter(Context context, Listener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void submitGroups(List<StudentGroup> newGroups) {
        groups.clear();
        membershipsByGroupId.clear();
        loadingGroupIds.clear();
        resolvedGroupIds.clear();
        errorGroupIds.clear();
        joiningGroupIds.clear();
        if (newGroups != null) {
            groups.addAll(newGroups);
        }
        notifyDataSetChanged();
    }

    public void markMembershipLoading(String groupId) {
        if (groupId == null) {
            return;
        }
        loadingGroupIds.add(groupId);
        errorGroupIds.remove(groupId);
        notifyDataSetChanged();
    }

    public void updateMembership(String groupId, GroupMembership membership) {
        if (groupId == null) {
            return;
        }
        loadingGroupIds.remove(groupId);
        joiningGroupIds.remove(groupId);
        errorGroupIds.remove(groupId);
        resolvedGroupIds.add(groupId);
        if (membership == null) {
            membershipsByGroupId.remove(groupId);
        } else {
            membershipsByGroupId.put(groupId, membership);
        }
        notifyDataSetChanged();
    }

    public void markMembershipError(String groupId) {
        if (groupId == null) {
            return;
        }
        loadingGroupIds.remove(groupId);
        joiningGroupIds.remove(groupId);
        membershipsByGroupId.remove(groupId);
        errorGroupIds.add(groupId);
        notifyDataSetChanged();
    }

    public void markJoining(String groupId) {
        if (groupId == null) {
            return;
        }
        joiningGroupIds.add(groupId);
        errorGroupIds.remove(groupId);
        notifyDataSetChanged();
    }

    public boolean isMembershipResolved(String groupId) {
        return resolvedGroupIds.contains(groupId);
    }

    public boolean hasMembershipError(String groupId) {
        return errorGroupIds.contains(groupId);
    }

    public boolean isBusy(String groupId) {
        return loadingGroupIds.contains(groupId) || joiningGroupIds.contains(groupId);
    }

    public GroupMembership getMembershipForGroup(String groupId) {
        return membershipsByGroupId.get(groupId);
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public StudentGroup getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_group, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        StudentGroup group = getItem(position);
        GroupMembership membership = membershipsByGroupId.get(group.getGroupId());

        holder.nameView.setText(group.getGroupName());
        holder.descriptionView.setText(group.getGroupDescription());
        holder.joinAsUserButton.setOnClickListener(null);
        holder.adminInfoButton.setOnClickListener(null);

        if (joiningGroupIds.contains(group.getGroupId())) {
            holder.statusView.setText("Joining...");
            holder.roleView.setText("Creating your membership");
            holder.actionContainer.setVisibility(View.VISIBLE);
            holder.joinAsUserButton.setEnabled(false);
            holder.adminInfoButton.setEnabled(false);
            holder.joinAsUserButton.setText("Joining...");
            holder.adminInfoButton.setText("Enroll As Admin");
            applySecondaryChip(holder, parent);
        } else if (loadingGroupIds.contains(group.getGroupId()) && !resolvedGroupIds.contains(group.getGroupId())) {
            holder.statusView.setText("Checking...");
            holder.roleView.setText("Checking whether you're already enrolled");
            holder.actionContainer.setVisibility(View.GONE);
            applySecondaryChip(holder, parent);
        } else if (errorGroupIds.contains(group.getGroupId())) {
            holder.statusView.setText("Retry");
            holder.roleView.setText("Couldn't load membership. Tap to retry.");
            holder.actionContainer.setVisibility(View.GONE);
            applySecondaryChip(holder, parent);
        } else if (membership == null) {
            holder.statusView.setText("Not Enrolled");
            holder.roleView.setText("Join as user below. Admin access is granted manually.");
            holder.actionContainer.setVisibility(View.VISIBLE);
            holder.joinAsUserButton.setEnabled(true);
            holder.adminInfoButton.setEnabled(true);
            holder.joinAsUserButton.setText("Join As User");
            holder.adminInfoButton.setText("Enroll As Admin");
            holder.joinAsUserButton.setOnClickListener(v -> listener.onJoinAsUser(group));
            holder.adminInfoButton.setOnClickListener(v -> listener.onAdminAccessInfo(group));
            applySecondaryChip(holder, parent);
        } else {
            holder.statusView.setText("Enrolled");
            holder.roleView.setText(membership.getRoleInGroup() == Role.ADMIN ? "Role in group: Admin" : "Role in group: User");
            holder.actionContainer.setVisibility(View.GONE);
            holder.statusView.setBackgroundResource(
                    membership.getRoleInGroup() == Role.ADMIN ? R.drawable.bg_chip_primary : R.drawable.bg_chip_tertiary
            );
            holder.statusView.setTextColor(parent.getResources().getColor(
                    membership.getRoleInGroup() == Role.ADMIN ? R.color.primary : R.color.tertiary,
                    parent.getContext().getTheme()
            ));
        }

        return convertView;
    }

    private void applySecondaryChip(ViewHolder holder, ViewGroup parent) {
        holder.statusView.setBackgroundResource(R.drawable.bg_chip_secondary);
        holder.statusView.setTextColor(parent.getResources().getColor(
                R.color.on_secondary_container,
                parent.getContext().getTheme()
        ));
    }

    private static final class ViewHolder {
        private final TextView nameView;
        private final TextView descriptionView;
        private final TextView statusView;
        private final TextView roleView;
        private final LinearLayout actionContainer;
        private final Button joinAsUserButton;
        private final Button adminInfoButton;

        private ViewHolder(View itemView) {
            this.nameView = itemView.findViewById(R.id.tv_group_name);
            this.descriptionView = itemView.findViewById(R.id.tv_group_description);
            this.statusView = itemView.findViewById(R.id.tv_group_status);
            this.roleView = itemView.findViewById(R.id.tv_group_role_hint);
            this.actionContainer = itemView.findViewById(R.id.layout_group_actions);
            this.joinAsUserButton = itemView.findViewById(R.id.btn_group_join_user);
            this.adminInfoButton = itemView.findViewById(R.id.btn_group_admin_info);
        }
    }
}
