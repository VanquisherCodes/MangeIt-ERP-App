package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request entity for approvals and submissions.
 */
public class Request {

    @SerializedName("request_id")
    private String requestId;
    @SerializedName(value = "request_type", alternate = {"type"})
    private String requestType;
    @SerializedName("description")
    private String description;
    @SerializedName("status")
    private String status;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("group_id")
    private String groupId;
    @SerializedName(value = "first_name", alternate = {"requester_first_name"})
    private String requesterFirstName;
    @SerializedName(value = "last_name", alternate = {"requester_last_name"})
    private String requesterLastName;
    @SerializedName(value = "email", alternate = {"requester_email"})
    private String requesterEmail;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getRequesterFirstName() {
        return requesterFirstName;
    }

    public void setRequesterFirstName(String requesterFirstName) {
        this.requesterFirstName = requesterFirstName;
    }

    public String getRequesterLastName() {
        return requesterLastName;
    }

    public void setRequesterLastName(String requesterLastName) {
        this.requesterLastName = requesterLastName;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public boolean isPending() {
        return "pending".equalsIgnoreCase(status);
    }

    public boolean isApproved() {
        return "approved".equalsIgnoreCase(status);
    }

    public boolean isRejected() {
        return "rejected".equalsIgnoreCase(status);
    }

    public String getRequesterDisplayName() {
        String first = requesterFirstName == null ? "" : requesterFirstName.trim();
        String last = requesterLastName == null ? "" : requesterLastName.trim();
        String fullName = (first + " " + last).trim();
        if (!fullName.isEmpty()) {
            return fullName;
        }
        if (requesterEmail != null && !requesterEmail.trim().isEmpty()) {
            return requesterEmail.trim();
        }
        if (userId != null && !userId.trim().isEmpty()) {
            return "User #" + userId.trim();
        }
        return "Student";
    }

    // Backward-compatible aliases.
    public String getId() {
        return getRequestId();
    }

    public void setId(String id) {
        setRequestId(id);
    }

    public String getRequesterId() {
        return getUserId();
    }

    public void setRequesterId(String requesterId) {
        setUserId(requesterId);
    }

    public String getType() {
        return getRequestType();
    }

    public void setType(String type) {
        setRequestType(type);
    }
}
