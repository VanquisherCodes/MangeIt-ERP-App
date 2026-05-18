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
import com.example.manageit.models.Request;
import com.example.manageit.models.Role;
import com.example.manageit.models.StudentGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Renders the group list and async membership/enrollment states.
 */
public class GroupListAdapter extends BaseAdapter {

    public interface Listener {
        void onRequestUserEnrollment(StudentGroup group);
        void onUnenroll(StudentGroup group, GroupMembership membership);
    }

    private final LayoutInflater inflater;
    private final Listener listener;
    private final List<StudentGroup> groups = new ArrayList<>();
    private final Map<String, GroupMembership> membershipsByGroupId = new HashMap<>();
    private final Map<String, Request> enrollmentRequestsByGroupId = new HashMap<>();
    private final Set<String> loadingGroupIds = new HashSet<>();
    private final Set<String> resolvedGroupIds = new HashSet<>();
    private final Set<String> errorGroupIds = new HashSet<>();
    private final Set<String> loadingEnrollmentRequestGroupIds = new HashSet<>();
    private final Set<String> enrollmentRequestErrorGroupIds = new HashSet<>();
    private final Set<String> submittingEnrollmentRequestGroupIds = new HashSet<>();
    private final Set<String> unenrollingGroupIds = new HashSet<>();

    public GroupListAdapter(Context context, Listener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void submitGroups(List<StudentGroup> newGroups) {
        groups.clear();
        membershipsByGroupId.clear();
        enrollmentRequestsByGroupId.clear();
        loadingGroupIds.clear();
        resolvedGroupIds.clear();
        errorGroupIds.clear();
        loadingEnrollmentRequestGroupIds.clear();
        enrollmentRequestErrorGroupIds.clear();
        submittingEnrollmentRequestGroupIds.clear();
        unenrollingGroupIds.clear();
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
        errorGroupIds.remove(groupId);
        unenrollingGroupIds.remove(groupId);
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
        membershipsByGroupId.remove(groupId);
        errorGroupIds.add(groupId);
        notifyDataSetChanged();
    }

    public void markEnrollmentRequestLoading(String groupId) {
        if (groupId == null) {
            return;
        }
        loadingEnrollmentRequestGroupIds.add(groupId);
        enrollmentRequestErrorGroupIds.remove(groupId);
        notifyDataSetChanged();
    }

    public void updateEnrollmentRequest(String groupId, Request request) {
        if (groupId == null) {
            return;
        }
        loadingEnrollmentRequestGroupIds.remove(groupId);
        submittingEnrollmentRequestGroupIds.remove(groupId);
        enrollmentRequestErrorGroupIds.remove(groupId);
        if (request == null) {
            enrollmentRequestsByGroupId.remove(groupId);
        } else {
            enrollmentRequestsByGroupId.put(groupId, request);
        }
        notifyDataSetChanged();
    }

    public void markEnrollmentRequestSubmitting(String groupId) {
        if (groupId == null) {
            return;
        }
        submittingEnrollmentRequestGroupIds.add(groupId);
        enrollmentRequestErrorGroupIds.remove(groupId);
        notifyDataSetChanged();
    }

    public void markEnrollmentRequestError(String groupId) {
        if (groupId == null) {
            return;
        }
        loadingEnrollmentRequestGroupIds.remove(groupId);
        submittingEnrollmentRequestGroupIds.remove(groupId);
        enrollmentRequestErrorGroupIds.add(groupId);
        notifyDataSetChanged();
    }

    public void markUnenrolling(String groupId, boolean unenrolling) {
        if (groupId == null) {
            return;
        }
        if (unenrolling) {
            unenrollingGroupIds.add(groupId);
        } else {
            unenrollingGroupIds.remove(groupId);
        }
        notifyDataSetChanged();
    }

    public boolean isMembershipResolved(String groupId) {
        return resolvedGroupIds.contains(groupId);
    }

    public boolean hasMembershipError(String groupId) {
        return errorGroupIds.contains(groupId);
    }

    public boolean hasPendingEnrollmentRequest(String groupId) {
        Request request = enrollmentRequestsByGroupId.get(groupId);
        return request != null && request.isPending();
    }

    public boolean hasApprovedEnrollmentRequest(String groupId) {
        Request request = enrollmentRequestsByGroupId.get(groupId);
        return request != null && request.isApproved();
    }

    public boolean hasRejectedEnrollmentRequest(String groupId) {
        Request request = enrollmentRequestsByGroupId.get(groupId);
        return request != null && request.isRejected();
    }

    public boolean isBusy(String groupId) {
        return loadingGroupIds.contains(groupId)
                || loadingEnrollmentRequestGroupIds.contains(groupId)
                || submittingEnrollmentRequestGroupIds.contains(groupId)
                || unenrollingGroupIds.contains(groupId);
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
        String groupId = group.getGroupId();
        GroupMembership membership = membershipsByGroupId.get(groupId);
        Request enrollmentRequest = enrollmentRequestsByGroupId.get(groupId);

        holder.nameView.setText(group.getGroupName());
        holder.descriptionView.setText(group.getGroupDescription());
        holder.joinAsUserButton.setOnClickListener(null);
        holder.unenrollButton.setOnClickListener(null);
        holder.joinAsUserButton.setVisibility(View.VISIBLE);
        holder.unenrollButton.setVisibility(View.GONE);

        if (loadingGroupIds.contains(groupId) && !resolvedGroupIds.contains(groupId)) {
            holder.statusView.setText("Checking...");
            holder.roleView.setText("Checking whether you're already enrolled");
            holder.actionContainer.setVisibility(View.GONE);
            applySecondaryChip(holder, parent);
            return convertView;
        }

        if (errorGroupIds.contains(groupId)) {
            holder.statusView.setText("Retry");
            holder.roleView.setText("Couldn't load membership. Tap to retry.");
            holder.actionContainer.setVisibility(View.GONE);
            applySecondaryChip(holder, parent);
            return convertView;
        }

        if (membership != null) {
            boolean unenrolling = unenrollingGroupIds.contains(groupId);
            holder.statusView.setText("Enrolled");
            holder.roleView.setText(unenrolling
                    ? "Unenrolling from this group..."
                    : membership.getRoleInGroup() == Role.ADMIN ? "Role in group: Admin" : "Role in group: User");
            holder.actionContainer.setVisibility(View.VISIBLE);
            holder.joinAsUserButton.setVisibility(View.GONE);
            holder.unenrollButton.setVisibility(View.VISIBLE);
            holder.unenrollButton.setEnabled(!unenrolling);
            holder.unenrollButton.setText("Unenroll");
            holder.unenrollButton.setContentDescription("Unenroll");
            holder.unenrollButton.setOnClickListener(v -> listener.onUnenroll(group, membership));
            holder.statusView.setBackgroundResource(
                    membership.getRoleInGroup() == Role.ADMIN ? R.drawable.bg_chip_primary : R.drawable.bg_chip_tertiary
            );
            holder.statusView.setTextColor(parent.getResources().getColor(
                    membership.getRoleInGroup() == Role.ADMIN ? R.color.primary : R.color.tertiary,
                    parent.getContext().getTheme()
            ));
            return convertView;
        }

        holder.actionContainer.setVisibility(View.VISIBLE);
        holder.joinAsUserButton.setEnabled(true);
        holder.joinAsUserButton.setText("Join As User");
        holder.joinAsUserButton.setOnClickListener(v -> listener.onRequestUserEnrollment(group));

        if (submittingEnrollmentRequestGroupIds.contains(groupId)) {
            holder.statusView.setText("Requesting...");
            holder.roleView.setText("Submitting your enrollment request for admin approval.");
            holder.joinAsUserButton.setEnabled(false);
            holder.joinAsUserButton.setText("Submitting...");
            applySecondaryChip(holder, parent);
            return convertView;
        }

        if (loadingEnrollmentRequestGroupIds.contains(groupId) && enrollmentRequest == null) {
            holder.statusView.setText("Checking...");
            holder.roleView.setText("Checking your join-request status.");
            holder.joinAsUserButton.setEnabled(false);
            holder.joinAsUserButton.setText("Join As User");
            applySecondaryChip(holder, parent);
            return convertView;
        }

        if (enrollmentRequestErrorGroupIds.contains(groupId) && enrollmentRequest == null) {
            holder.statusView.setText("Not Enrolled");
            holder.roleView.setText("Join as user or retry your join request.");
            holder.joinAsUserButton.setEnabled(true);
            holder.joinAsUserButton.setText("Join As User");
            applySecondaryChip(holder, parent);
            return convertView;
        }

        if (enrollmentRequest == null) {
            holder.statusView.setText("Not Enrolled");
            holder.roleView.setText("Join as user to request admin approval.");
            holder.joinAsUserButton.setEnabled(true);
            holder.joinAsUserButton.setText("Join As User");
            applySecondaryChip(holder, parent);
            return convertView;
        }

        if (enrollmentRequest.isPending()) {
            holder.statusView.setText("Join Pending");
            holder.roleView.setText("Your enrollment request is pending admin approval.");
            holder.joinAsUserButton.setEnabled(false);
            holder.joinAsUserButton.setText("Pending Approval");
            holder.statusView.setBackgroundResource(R.drawable.bg_chip_tertiary);
            holder.statusView.setTextColor(parent.getResources().getColor(R.color.tertiary, parent.getContext().getTheme()));
            return convertView;
        }

        if (enrollmentRequest.isApproved()) {
            holder.statusView.setText("Join Approved");
            holder.roleView.setText("Approved. Your membership should appear after the next refresh.");
            holder.joinAsUserButton.setEnabled(false);
            holder.joinAsUserButton.setText("Approved");
            holder.statusView.setBackgroundResource(R.drawable.bg_chip_primary);
            holder.statusView.setTextColor(parent.getResources().getColor(R.color.primary, parent.getContext().getTheme()));
            return convertView;
        }

        if (enrollmentRequest.isRejected()) {
            holder.statusView.setText("Join Rejected");
            holder.roleView.setText("Request rejected. You can submit again.");
            holder.joinAsUserButton.setEnabled(true);
            holder.joinAsUserButton.setText("Request Again");
            holder.statusView.setBackgroundResource(R.drawable.bg_chip_error);
            holder.statusView.setTextColor(parent.getResources().getColor(R.color.error, parent.getContext().getTheme()));
            return convertView;
        }

        holder.statusView.setText("Not Enrolled");
        holder.roleView.setText("Join as user to request admin approval.");
        holder.joinAsUserButton.setEnabled(true);
        holder.joinAsUserButton.setText("Join As User");
        applySecondaryChip(holder, parent);
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

        private ViewHolder(View itemView) {
            this.nameView = itemView.findViewById(R.id.tv_group_name);
            this.descriptionView = itemView.findViewById(R.id.tv_group_description);
            this.statusView = itemView.findViewById(R.id.tv_group_status);
            this.roleView = itemView.findViewById(R.id.tv_group_role_hint);
            this.actionContainer = itemView.findViewById(R.id.layout_group_actions);
            this.joinAsUserButton = itemView.findViewById(R.id.btn_group_join_user);
            this.unenrollButton = itemView.findViewById(R.id.btn_group_unenroll);
        }

        private final Button unenrollButton;
    }
}
