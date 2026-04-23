package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Task-level budget allocation and cost tracking row.
 */
public class TaskBudget {

    @SerializedName("task_budget_id")
    private String taskBudgetId;
    @SerializedName("budget_id")
    private String budgetId;
    @SerializedName("budget_category_id")
    private String budgetCategoryId;
    @SerializedName("task_id")
    private String taskId;
    @SerializedName("task_name")
    private String taskName;
    @SerializedName("category_name")
    private String categoryName;
    private String priority;
    @SerializedName("estimated_hours")
    private String estimatedHours;
    @SerializedName("team_size")
    private String teamSize;
    @SerializedName("allocated_amount")
    private String allocatedAmount;
    @SerializedName("predicted_cost_amount")
    private String predictedCostAmount;
    @SerializedName("actual_cost_amount")
    private String actualCostAmount;
    @SerializedName("variance_from_allocated")
    private String varianceFromAllocated;
    @SerializedName("variance_from_predicted")
    private String varianceFromPredicted;
    @SerializedName("completion_status")
    private String completionStatus;
    @SerializedName("ml_prediction_status")
    private String mlPredictionStatus;
    @SerializedName("ml_model_version")
    private String mlModelVersion;
    @SerializedName("prediction_generated_at")
    private String predictionGeneratedAt;

    public String getTaskBudgetId() {
        return taskBudgetId;
    }

    public void setTaskBudgetId(String taskBudgetId) {
        this.taskBudgetId = taskBudgetId;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getBudgetCategoryId() {
        return budgetCategoryId;
    }

    public void setBudgetCategoryId(String budgetCategoryId) {
        this.budgetCategoryId = budgetCategoryId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public String getPredictedCostAmount() {
        return predictedCostAmount;
    }

    public void setPredictedCostAmount(String predictedCostAmount) {
        this.predictedCostAmount = predictedCostAmount;
    }

    public String getActualCostAmount() {
        return actualCostAmount;
    }

    public void setActualCostAmount(String actualCostAmount) {
        this.actualCostAmount = actualCostAmount;
    }

    public String getVarianceFromAllocated() {
        return varianceFromAllocated;
    }

    public void setVarianceFromAllocated(String varianceFromAllocated) {
        this.varianceFromAllocated = varianceFromAllocated;
    }

    public String getVarianceFromPredicted() {
        return varianceFromPredicted;
    }

    public void setVarianceFromPredicted(String varianceFromPredicted) {
        this.varianceFromPredicted = varianceFromPredicted;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    public String getMlPredictionStatus() {
        return mlPredictionStatus;
    }

    public void setMlPredictionStatus(String mlPredictionStatus) {
        this.mlPredictionStatus = mlPredictionStatus;
    }

    public String getMlModelVersion() {
        return mlModelVersion;
    }

    public void setMlModelVersion(String mlModelVersion) {
        this.mlModelVersion = mlModelVersion;
    }

    public String getPredictionGeneratedAt() {
        return predictionGeneratedAt;
    }

    public void setPredictionGeneratedAt(String predictionGeneratedAt) {
        this.predictionGeneratedAt = predictionGeneratedAt;
    }

    public String getId() {
        return getTaskBudgetId();
    }

    public void setId(String id) {
        setTaskBudgetId(id);
    }
}
