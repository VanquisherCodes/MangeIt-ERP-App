package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Join table between user and student group.
 */
public class GroupMembership {
    @SerializedName("membership_id")
    private String membershipId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("group_id")
    private String groupId;
    @SerializedName("role_in_group")
    private Role roleInGroup;
    @SerializedName("joined_at")
    private String joinedAt;
    @SerializedName("membership_status")
    private String membershipStatus;

    public GroupMembership() {
    }

    public GroupMembership(
            String membershipId,
            String userId,
            String groupId,
            Role roleInGroup,
            String joinedAt,
            String membershipStatus
    ) {
        this.membershipId = membershipId;
        this.userId = userId;
        this.groupId = groupId;
        this.roleInGroup = roleInGroup;
        this.joinedAt = joinedAt;
        this.membershipStatus = membershipStatus;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
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

    public Role getRoleInGroup() {
        return roleInGroup;
    }

    public void setRoleInGroup(Role roleInGroup) {
        this.roleInGroup = roleInGroup;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }
}
