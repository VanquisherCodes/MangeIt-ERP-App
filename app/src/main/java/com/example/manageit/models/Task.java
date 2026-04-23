package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Task entity for ERP task management.
 */
public class Task {
    @SerializedName("task_id")
    private String id;
    @SerializedName("group_id")
    private String groupId;
    private String title;
    private String description;
    private String status;
    @SerializedName("due_date")
    private String dueDate;
    @SerializedName("created_at")
    private String createdAt;
    private String priority;
    @SerializedName("estimated_hours")
    private String estimatedHours;
    @SerializedName("team_size")
    private String teamSize;
    @SerializedName("assigned_to_membership_id")
    private String assignedToMembershipId;
    @SerializedName("assigned_by_membership_id")
    private String assignedByMembershipId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(String estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public String getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(String teamSize) {
        this.teamSize = teamSize;
    }

    public String getAssignedToMembershipId() {
        return assignedToMembershipId;
    }

    public void setAssignedToMembershipId(String assignedToMembershipId) {
        this.assignedToMembershipId = assignedToMembershipId;
    }

    public String getAssignedByMembershipId() {
        return assignedByMembershipId;
    }

    public void setAssignedByMembershipId(String assignedByMembershipId) {
        this.assignedByMembershipId = assignedByMembershipId;
    }

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }
}
