package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Announcement entity for group-wide communication.
 */
public class Announcement {
    @SerializedName("announcement_id")
    private String id;
    @SerializedName("group_id")
    private String groupId;
    private String title;
    @SerializedName("message")
    private String body;
    @SerializedName("created_at")
    private String publishedAt;
    @SerializedName("created_by_membership_id")
    private String createdByMembershipId;

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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getCreatedByMembershipId() {
        return createdByMembershipId;
    }

    public void setCreatedByMembershipId(String createdByMembershipId) {
        this.createdByMembershipId = createdByMembershipId;
    }
}
