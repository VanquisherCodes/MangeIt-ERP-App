package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Itemized spending row linked to a task budget.
 */
public class Expense {

    @SerializedName("expense_id")
    private String expenseId;
    @SerializedName("task_budget_id")
    private String taskBudgetId;
    @SerializedName("expense_title")
    private String expenseTitle;
    @SerializedName("expense_amount")
    private String expenseAmount;
    @SerializedName("expense_date")
    private String expenseDate;
    @SerializedName("vendor_name")
    private String vendorName;
    private String notes;
    @SerializedName("created_by_membership_id")
    private String createdByMembershipId;
    @SerializedName("created_at")
    private String createdAt;

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getTaskBudgetId() {
        return taskBudgetId;
    }

    public void setTaskBudgetId(String taskBudgetId) {
        this.taskBudgetId = taskBudgetId;
    }

    public String getExpenseTitle() {
        return expenseTitle;
    }

    public void setExpenseTitle(String expenseTitle) {
        this.expenseTitle = expenseTitle;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getId() {
        return getExpenseId();
    }

    public void setId(String id) {
        setExpenseId(id);
    }
}
