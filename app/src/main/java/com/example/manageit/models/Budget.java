package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Budget header entity for one student group.
 */
public class Budget {

    @SerializedName("budget_id")
    private String budgetId;
    @SerializedName("group_id")
    private String groupId;
    private String name;
    private String description;
    @SerializedName("total_amount")
    private String totalAmount;
    @SerializedName("currency_code")
    private String currencyCode;
    @SerializedName("period_start")
    private String periodStart;
    @SerializedName("period_end")
    private String periodEnd;
    private String status;
    @SerializedName("created_by_membership_id")
    private String createdByMembershipId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedByMembershipId() {
        return createdByMembershipId;
    }

    public void setCreatedByMembershipId(String createdByMembershipId) {
        this.createdByMembershipId = createdByMembershipId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return getBudgetId();
    }

    public void setId(String id) {
        setBudgetId(id);
    }
}
