package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Aggregated budget totals shown on the dashboard.
 */
public class BudgetOverview {

    @SerializedName("budget_id")
    private String budgetId;
    private String name;
    @SerializedName("total_budget")
    private String totalBudget;
    @SerializedName("total_allocated")
    private String totalAllocated;
    @SerializedName("total_spent")
    private String totalSpent;
    @SerializedName("remaining_budget")
    private String remainingBudget;
    @SerializedName("unallocated_budget")
    private String unallocatedBudget;
    @SerializedName("currency_code")
    private String currencyCode;
    private String status;
    @SerializedName("period_start")
    private String periodStart;
    @SerializedName("period_end")
    private String periodEnd;

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(String totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getTotalAllocated() {
        return totalAllocated;
    }

    public void setTotalAllocated(String totalAllocated) {
        this.totalAllocated = totalAllocated;
    }

    public String getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(String totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(String remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public String getUnallocatedBudget() {
        return unallocatedBudget;
    }

    public void setUnallocatedBudget(String unallocatedBudget) {
        this.unallocatedBudget = unallocatedBudget;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
