package com.example.manageit.backend.dto;

public class PredictTaskCostRequest {

    private String groupId;
    private String categoryName;
    private String priority;
    private String estimatedHours;
    private String teamSize;
    private String allocatedAmount;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(String allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }
}
