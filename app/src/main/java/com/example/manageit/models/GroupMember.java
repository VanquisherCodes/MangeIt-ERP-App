package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Joined user + membership projection for admin membership management.
 */
public class GroupMember {
    @SerializedName("membership_id")
    private String membershipId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("group_id")
    private String groupId;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    private String email;
    @SerializedName("role_in_group")
    private Role roleInGroup;
    @SerializedName("membership_status")
    private String membershipStatus;
    @SerializedName("joined_at")
    private String joinedAt;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRoleInGroup() {
        return roleInGroup;
    }

    public void setRoleInGroup(Role roleInGroup) {
        this.roleInGroup = roleInGroup;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getDisplayName() {
        String fullName = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
        return fullName.isEmpty() ? "Member" : fullName;
    }
}
