package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Category-level budget breakdown for a group budget.
 */
public class BudgetCategory {

    @SerializedName("budget_category_id")
    private String budgetCategoryId;
    @SerializedName("budget_id")
    private String budgetId;
    @SerializedName("category_name")
    private String categoryName;
    @SerializedName("allocated_amount")
    private String allocatedAmount;
    @SerializedName("allocated_to_tasks")
    private String allocatedToTasks;
    @SerializedName("spent_amount")
    private String spentAmount;
    @SerializedName("remaining_to_allocate")
    private String remainingToAllocate;
    @SerializedName("remaining_to_spend")
    private String remainingToSpend;
    private String notes;
    @SerializedName("display_order")
    private String displayOrder;

    public String getBudgetCategoryId() {
        return budgetCategoryId;
    }

    public void setBudgetCategoryId(String budgetCategoryId) {
        this.budgetCategoryId = budgetCategoryId;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(String allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public String getAllocatedToTasks() {
        return allocatedToTasks;
    }

    public void setAllocatedToTasks(String allocatedToTasks) {
        this.allocatedToTasks = allocatedToTasks;
    }

    public String getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(String spentAmount) {
        this.spentAmount = spentAmount;
    }

    public String getRemainingToAllocate() {
        return remainingToAllocate;
    }

    public void setRemainingToAllocate(String remainingToAllocate) {
        this.remainingToAllocate = remainingToAllocate;
    }

    public String getRemainingToSpend() {
        return remainingToSpend;
    }

    public void setRemainingToSpend(String remainingToSpend) {
        this.remainingToSpend = remainingToSpend;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getId() {
        return getBudgetCategoryId();
    }

    public void setId(String id) {
        setBudgetCategoryId(id);
    }
}
