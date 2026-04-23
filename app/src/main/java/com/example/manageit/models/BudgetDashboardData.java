package com.example.manageit.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated budget screen payload for one selected group.
 */
public class BudgetDashboardData {

    private Budget activeBudget;
    private BudgetOverview overview;
    private List<BudgetCategory> categories = new ArrayList<>();
    private List<TaskBudget> taskBudgets = new ArrayList<>();

    public Budget getActiveBudget() {
        return activeBudget;
    }

    public void setActiveBudget(Budget activeBudget) {
        this.activeBudget = activeBudget;
    }

    public BudgetOverview getOverview() {
        return overview;
    }

    public void setOverview(BudgetOverview overview) {
        this.overview = overview;
    }

    public List<BudgetCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<BudgetCategory> categories) {
        this.categories = categories == null ? new ArrayList<>() : new ArrayList<>(categories);
    }

    public List<TaskBudget> getTaskBudgets() {
        return taskBudgets;
    }

    public void setTaskBudgets(List<TaskBudget> taskBudgets) {
        this.taskBudgets = taskBudgets == null ? new ArrayList<>() : new ArrayList<>(taskBudgets);
    }
}
