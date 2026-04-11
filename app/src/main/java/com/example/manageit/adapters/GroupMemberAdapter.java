package com.example.manageit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.manageit.R;
import com.example.manageit.models.GroupMember;
import com.example.manageit.models.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Renders members for the admin membership-management screen.
 */
public class GroupMemberAdapter extends BaseAdapter {

    public interface Listener {
        void onToggleAdmin(GroupMember member);
    }

    private final LayoutInflater inflater;
    private final Listener listener;
    private final String currentUserId;
    private final List<GroupMember> members = new ArrayList<>();
    private final Set<String> loadingMembershipIds = new HashSet<>();

    public GroupMemberAdapter(Context context, String currentUserId, Listener listener) {
        this.inflater = LayoutInflater.from(context);
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    public void submitMembers(List<GroupMember> newMembers) {
        members.clear();
        if (newMembers != null) {
            members.addAll(newMembers);
        }
        notifyDataSetChanged();
    }

    public void markUpdating(String membershipId, boolean updating) {
        if (membershipId == null) {
            return;
        }
        if (updating) {
            loadingMembershipIds.add(membershipId);
        } else {
            loadingMembershipIds.remove(membershipId);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public GroupMember getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_group_member, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GroupMember member = getItem(position);
        boolean isUpdating = loadingMembershipIds.contains(member.getMembershipId());
        boolean isCurrentAdmin = currentUserId != null
                && currentUserId.equals(member.getUserId())
                && member.getRoleInGroup() == Role.ADMIN;

        holder.nameView.setText(member.getDisplayName());
        holder.emailView.setText(member.getEmail() == null ? "" : member.getEmail());
        holder.roleView.setText(member.getRoleInGroup() == Role.ADMIN ? "Admin" : "User");
        holder.roleView.setBackgroundResource(
                member.getRoleInGroup() == Role.ADMIN ? R.drawable.bg_chip_primary : R.drawable.bg_chip_tertiary
        );
        holder.roleView.setTextColor(parent.getResources().getColor(
                member.getRoleInGroup() == Role.ADMIN ? R.color.primary : R.color.tertiary,
                parent.getContext().getTheme()
        ));
        holder.statusView.setText("Status: " + (member.getMembershipStatus() == null ? "active" : member.getMembershipStatus()));

        if (isCurrentAdmin) {
            holder.toggleRoleButton.setEnabled(false);
            holder.toggleRoleButton.setText("Current Admin");
        } else if (isUpdating) {
            holder.toggleRoleButton.setEnabled(false);
            holder.toggleRoleButton.setText("Updating...");
        } else if (member.getRoleInGroup() == Role.ADMIN) {
            holder.toggleRoleButton.setEnabled(true);
            holder.toggleRoleButton.setText("Remove Admin");
        } else {
            holder.toggleRoleButton.setEnabled(true);
            holder.toggleRoleButton.setText("Make Admin");
        }

        holder.toggleRoleButton.setOnClickListener(v -> {
            if (!isUpdating && !isCurrentAdmin) {
                listener.onToggleAdmin(member);
            }
        });

        return convertView;
    }

    private static final class ViewHolder {
        private final TextView nameView;
        private final TextView emailView;
        private final TextView roleView;
        private final TextView statusView;
        private final Button toggleRoleButton;

        private ViewHolder(View itemView) {
            this.nameView = itemView.findViewById(R.id.tv_member_name);
            this.emailView = itemView.findViewById(R.id.tv_member_email);
            this.roleView = itemView.findViewById(R.id.tv_member_role);
            this.statusView = itemView.findViewById(R.id.tv_member_status);
            this.toggleRoleButton = itemView.findViewById(R.id.btn_toggle_member_role);
        }
    }
}
