package com.example.manageit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.manageit.R;
import com.example.manageit.models.Request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Renders enrollment requests and approval actions.
 */
public class AdminAccessRequestAdapter extends BaseAdapter {

    public interface Listener {
        void onApprove(Request request);

        void onReject(Request request);
    }

    private final LayoutInflater inflater;
    private final Listener listener;
    private final List<Request> requests = new ArrayList<>();
    private final Set<String> updatingRequestIds = new HashSet<>();

    public AdminAccessRequestAdapter(Context context, Listener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void submitRequests(List<Request> newRequests) {
        requests.clear();
        if (newRequests != null) {
            requests.addAll(newRequests);
        }
        notifyDataSetChanged();
    }

    public void markUpdating(String requestId, boolean updating) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return;
        }
        if (updating) {
            updatingRequestIds.add(requestId);
        } else {
            updatingRequestIds.remove(requestId);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Request getItem(int position) {
        return requests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_admin_access_request, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Request request = getItem(position);
        String requestId = request.getRequestId() == null ? "" : request.getRequestId();
        boolean isUpdating = updatingRequestIds.contains(requestId);

        holder.nameView.setText(request.getRequesterDisplayName());
        holder.emailView.setText(request.getRequesterEmail() == null ? "" : request.getRequesterEmail());
        holder.descriptionView.setText(
                formatDescription(request.getDescription())
        );
        holder.createdAtView.setText(
                request.getCreatedAt() == null || request.getCreatedAt().trim().isEmpty()
                        ? "Created date unavailable"
                        : "Submitted: " + request.getCreatedAt().trim()
        );

        String statusLabel = normalizeStatus(request.getStatus());
        holder.statusView.setText(statusLabel);
        applyStatusStyle(holder, parent, request);

        if (isUpdating) {
            holder.approveButton.setEnabled(false);
            holder.rejectButton.setEnabled(false);
            holder.approveButton.setText("Updating...");
            holder.rejectButton.setText("Updating...");
        } else if (request.isPending()) {
            holder.approveButton.setEnabled(true);
            holder.rejectButton.setEnabled(true);
            holder.approveButton.setText("Approve");
            holder.rejectButton.setText("Reject");
        } else {
            holder.approveButton.setEnabled(false);
            holder.rejectButton.setEnabled(false);
            holder.approveButton.setText("Approve");
            holder.rejectButton.setText("Reject");
        }

        holder.approveButton.setOnClickListener(v -> {
            if (!isUpdating && request.isPending()) {
                listener.onApprove(request);
            }
        });
        holder.rejectButton.setOnClickListener(v -> {
            if (!isUpdating && request.isPending()) {
                listener.onReject(request);
            }
        });

        return convertView;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "PENDING";
        }
        return status.trim().toUpperCase();
    }

    private String formatDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "Requested to join this group as a standard user.";
        }

        String normalized = description.trim();
        if ("requested_user_enrollment".equalsIgnoreCase(normalized)) {
            return "Requested to join this group as a standard user.";
        }

        return normalized.replace('_', ' ');
    }

    private void applyStatusStyle(ViewHolder holder, ViewGroup parent, Request request) {
        if (request.isApproved()) {
            holder.statusView.setBackgroundResource(R.drawable.bg_chip_primary);
            holder.statusView.setTextColor(parent.getResources().getColor(R.color.primary, parent.getContext().getTheme()));
            return;
        }
        if (request.isRejected()) {
            holder.statusView.setBackgroundResource(R.drawable.bg_chip_error);
            holder.statusView.setTextColor(parent.getResources().getColor(R.color.error, parent.getContext().getTheme()));
            return;
        }
        holder.statusView.setBackgroundResource(R.drawable.bg_chip_tertiary);
        holder.statusView.setTextColor(parent.getResources().getColor(R.color.tertiary, parent.getContext().getTheme()));
    }

    private static final class ViewHolder {
        private final TextView nameView;
        private final TextView emailView;
        private final TextView descriptionView;
        private final TextView createdAtView;
        private final TextView statusView;
        private final Button approveButton;
        private final Button rejectButton;

        private ViewHolder(View itemView) {
            this.nameView = itemView.findViewById(R.id.tv_access_request_name);
            this.emailView = itemView.findViewById(R.id.tv_access_request_email);
            this.descriptionView = itemView.findViewById(R.id.tv_access_request_description);
            this.createdAtView = itemView.findViewById(R.id.tv_access_request_created_at);
            this.statusView = itemView.findViewById(R.id.tv_access_request_status);
            this.approveButton = itemView.findViewById(R.id.btn_approve_access_request);
            this.rejectButton = itemView.findViewById(R.id.btn_reject_access_request);
        }
    }
}
