package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Student group returned by the backend services.
 */
public class StudentGroup {
    @SerializedName("group_id")
    private String groupId;
    @SerializedName("group_name")
    private String groupName;
    @SerializedName("group_description")
    private String groupDescription;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("is_active")
    private int activeValue;

    public StudentGroup() {
    }

    public StudentGroup(String groupId, String groupName, String groupDescription, String createdAt, boolean active) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdAt = createdAt;
        this.activeValue = active ? 1 : 0;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return activeValue == 1;
    }

    public void setActive(boolean active) {
        this.activeValue = active ? 1 : 0;
    }
}
