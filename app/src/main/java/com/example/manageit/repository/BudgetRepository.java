package com.example.manageit.repository;

import android.net.Uri;
import androidx.annotation.Nullable;
import android.util.Log;

import com.example.manageit.errors.ApiErrorMapper;
import com.example.manageit.models.Budget;
import com.example.manageit.models.BudgetCategory;
import com.example.manageit.models.BudgetDashboardData;
import com.example.manageit.models.BudgetOverview;
import com.example.manageit.models.Expense;
import com.example.manageit.models.TaskBudget;
import com.example.manageit.network.ApiClient;
import com.example.manageit.repository.contracts.BudgetRepositoryContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Budget read operations scoped to one student group.
 */
public class BudgetRepository implements BudgetRepositoryContract {

    private static final String TAG = "BudgetRepository";
    private static final String STUDEV_SPACE_TOKEN = "__sp__";

    private final ApiClient apiClient;

    public BudgetRepository() {
        this(ApiClient.getInstance());
    }

    public BudgetRepository(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void createBudget(
            String groupId,
            String name,
            String description,
            String totalAmount,
            String currencyCode,
            String periodStart,
            String periodEnd,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .createBudget(
                        groupId,
                        encodeRequiredPathValue(name),
                        normalizeOptionalPathValue(description),
                        totalAmount,
                        encodeRequiredPathValue(currencyCode),
                        periodStart,
                        periodEnd,
                        encodeRequiredPathValue("active"),
                        createdByMembershipId,
                        createdByMembershipId,
                        groupId,
                        totalAmount,
                        periodEnd,
                        periodStart,
                        "active",
                        groupId
                )
                .enqueue(new WriteCallback(
                        callback,
                        "Couldn't create the group budget. Check your values and try again."
                ));
    }

    public void createBudgetCategory(
            String budgetId,
            String categoryName,
            String allocatedAmount,
            String notes,
            String displayOrder,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .createBudgetCategory(
                        budgetId,
                        encodeRequiredPathValue(categoryName),
                        allocatedAmount,
                        normalizeOptionalPathValue(notes),
                        displayOrder,
                        createdByMembershipId,
                        budgetId,
                        allocatedAmount,
                        allocatedAmount
                )
                .enqueue(new WriteCallback(
                        callback,
                        "Couldn't create the budget category. Make sure it fits within the budget."
                ));
    }

    public void createTaskBudget(
            String budgetId,
            String budgetCategoryId,
            String taskId,
            String allocatedAmount,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .createTaskBudget(
                        budgetId,
                        budgetCategoryId,
                        taskId,
                        allocatedAmount,
                        createdByMembershipId,
                        taskId,
                        createdByMembershipId,
                        budgetCategoryId,
                        budgetId,
                        budgetId,
                        taskId,
                        allocatedAmount,
                        allocatedAmount
                )
                .enqueue(new WriteCallback(
                        callback,
                        "Couldn't allocate this task budget. The category may not have enough remaining budget."
                ));
    }

    public void updateBudgetCategoryAllocation(
            String budgetCategoryId,
            String allocatedAmount,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .updateBudgetCategoryAllocation(
                        createdByMembershipId,
                        allocatedAmount,
                        budgetCategoryId,
                        allocatedAmount,
                        allocatedAmount,
                        allocatedAmount
                )
                .enqueue(new WriteCallback(
                        callback,
                        "Couldn't update this category allocation. It must stay within the total budget and above current task allocations."
                ));
    }

    public void updateTaskBudgetAllocation(
            String taskBudgetId,
            String allocatedAmount,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .updateTaskBudgetAllocation(
                        createdByMembershipId,
                        allocatedAmount,
                        taskBudgetId,
                        allocatedAmount,
                        allocatedAmount
                )
                .enqueue(new WriteCallback(
                        callback,
                        "Couldn't update this task allocation. The category may not have enough remaining budget."
                ));
    }

    public void updateTaskBudgetActualCost(
            String taskBudgetId,
            String actualCostAmount,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .updateTaskBudgetActualCost(
                        createdByMembershipId,
                        actualCostAmount,
                        taskBudgetId,
                        actualCostAmount
                )
                .enqueue(new WriteCallback(
                        callback,
                        "Couldn't update the actual cost for this task budget."
                ));
    }

    public void createExpense(
            String taskBudgetId,
            String expenseTitle,
            String expenseAmount,
            String expenseDate,
            String vendorName,
            String notes,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .createExpense(
                        normalizeExpenseTitlePathValue(expenseTitle),
                        expenseAmount,
                        encodeRequiredPathValue(expenseDate),
                        normalizeOptionalPathValue(vendorName),
                        normalizeOptionalPathValue(notes),
                        createdByMembershipId,
                        createdByMembershipId,
                        taskBudgetId,
                        expenseAmount
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String successBody = readBody(response.body());
                        String errorBody = readBody(response.errorBody());
                        Log.d(TAG, "CreateExpense response"
                                + " taskBudgetId=" + taskBudgetId
                                + " membershipId=" + createdByMembershipId
                                + " code=" + response.code()
                                + " successBody=" + successBody
                                + " errorBody=" + errorBody);

                        if (!response.isSuccessful() || isZeroRowsAffected(successBody)) {
                            String details = firstNonEmpty(errorBody, successBody);
                            if (details == null || details.trim().isEmpty()) {
                                details = "The backend returned no rows. This usually means the task budget id or admin membership id did not match.";
                            }
                            callback.onError("Couldn't create the expense for this task budget. " + details);
                            return;
                        }

                        syncTaskBudgetActualFromExpenses(taskBudgetId, createdByMembershipId, callback);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while creating the expense.");
                    }
                });
    }

    public void getTaskBudgetExpenses(String taskBudgetId, RepositoryCallback<List<Expense>> callback) {
        apiClient.getApiService().getTaskBudgetExpenses(taskBudgetId).enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Couldn't load expenses for this task budget.");
                    return;
                }

                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable throwable) {
                callback.onError("Couldn't reach the server while loading expenses.");
            }
        });
    }

    public void syncTaskBudgetActualFromExpenses(
            String taskBudgetId,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .syncTaskBudgetActualFromExpenses(taskBudgetId, createdByMembershipId)
                .enqueue(new WriteCallback(
                        callback,
                        "Couldn't sync actual cost from expenses."
                ));
    }

    public void getBudgetDashboardData(String groupId, RepositoryCallback<BudgetDashboardData> callback) {
        BudgetDashboardData data = new BudgetDashboardData();
        List<String> errors = new ArrayList<>();

        loadActiveBudget(groupId, data, errors, callback);
    }

    private void loadActiveBudget(
            String groupId,
            BudgetDashboardData data,
            List<String> errors,
            RepositoryCallback<BudgetDashboardData> callback
    ) {
        apiClient.getApiService().getActiveGroupBudget(groupId).enqueue(new Callback<List<Budget>>() {
            @Override
            public void onResponse(Call<List<Budget>> call, Response<List<Budget>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    data.setActiveBudget(response.body().get(0));
                }
                loadOverview(groupId, data, errors, callback);
            }

            @Override
            public void onFailure(Call<List<Budget>> call, Throwable throwable) {
                errors.add("budget");
                loadOverview(groupId, data, errors, callback);
            }
        });
    }

    private void loadOverview(
            String groupId,
            BudgetDashboardData data,
            List<String> errors,
            RepositoryCallback<BudgetDashboardData> callback
    ) {
        apiClient.getApiService().getGroupBudgetOverview(groupId).enqueue(new Callback<List<BudgetOverview>>() {
            @Override
            public void onResponse(Call<List<BudgetOverview>> call, Response<List<BudgetOverview>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    data.setOverview(response.body().get(0));
                } else {
                    errors.add("overview");
                }
                loadCategories(groupId, data, errors, callback);
            }

            @Override
            public void onFailure(Call<List<BudgetOverview>> call, Throwable throwable) {
                errors.add("overview");
                loadCategories(groupId, data, errors, callback);
            }
        });
    }

    private void loadCategories(
            String groupId,
            BudgetDashboardData data,
            List<String> errors,
            RepositoryCallback<BudgetDashboardData> callback
    ) {
        apiClient.getApiService().getGroupBudgetCategories(groupId).enqueue(new Callback<List<BudgetCategory>>() {
            @Override
            public void onResponse(Call<List<BudgetCategory>> call, Response<List<BudgetCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setCategories(response.body());
                } else {
                    errors.add("categories");
                }
                loadTaskBudgets(groupId, data, errors, callback);
            }

            @Override
            public void onFailure(Call<List<BudgetCategory>> call, Throwable throwable) {
                errors.add("categories");
                loadTaskBudgets(groupId, data, errors, callback);
            }
        });
    }

    private void loadTaskBudgets(
            String groupId,
            BudgetDashboardData data,
            List<String> errors,
            RepositoryCallback<BudgetDashboardData> callback
    ) {
        apiClient.getApiService().getGroupTaskBudgets(groupId).enqueue(new Callback<List<TaskBudget>>() {
            @Override
            public void onResponse(Call<List<TaskBudget>> call, Response<List<TaskBudget>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setTaskBudgets(response.body());
                } else {
                    errors.add("tasks");
                }
                finishDashboardLoad(data, errors, callback);
            }

            @Override
            public void onFailure(Call<List<TaskBudget>> call, Throwable throwable) {
                errors.add("tasks");
                finishDashboardLoad(data, errors, callback);
            }
        });
    }

    private void finishDashboardLoad(
            BudgetDashboardData data,
            List<String> errors,
            RepositoryCallback<BudgetDashboardData> callback
    ) {
        boolean hasAnyData = data.getActiveBudget() != null
                || data.getOverview() != null
                || !data.getCategories().isEmpty()
                || !data.getTaskBudgets().isEmpty();

        if (hasAnyData) {
            callback.onSuccess(data);
            return;
        }

        if (!errors.isEmpty()) {
            callback.onError("Couldn't load the group budget right now.");
            return;
        }

        callback.onSuccess(data);
    }

    private String normalizeOptionalPathValue(String rawValue) {
        if (rawValue == null) {
            return "null";
        }

        String trimmed = rawValue.trim();
        return trimmed.isEmpty() ? "null" : encodeRequiredPathValue(trimmed);
    }

    private String encodeRequiredPathValue(String rawValue) {
        String trimmed = rawValue == null ? "" : rawValue.trim();
        return Uri.encode(trimmed);
    }

    private String normalizeExpenseTitlePathValue(String rawValue) {
        String trimmed = rawValue == null ? "" : rawValue.trim();
        return trimmed.replace(" ", STUDEV_SPACE_TOKEN);
    }

    private boolean isZeroRowsAffected(@Nullable String rawBody) {
        if (rawBody == null) {
            return false;
        }

        String normalized = rawBody.toLowerCase();
        return normalized.contains("0 row") || normalized.contains("0 rows affected");
    }

    private @Nullable String readBody(ResponseBody body) {
        if (body == null) {
            return null;
        }

        try {
            return body.string();
        } catch (IOException ignored) {
            return null;
        }
    }

    private @Nullable String firstNonEmpty(@Nullable String first, @Nullable String second) {
        if (first != null && !first.trim().isEmpty()) {
            return first.trim();
        }
        if (second != null && !second.trim().isEmpty()) {
            return second.trim();
        }
        return null;
    }

    private class WriteCallback implements Callback<ResponseBody> {

        private final RepositoryCallback<Void> callback;
        private final String fallbackError;

        private WriteCallback(RepositoryCallback<Void> callback, String fallbackError) {
            this.callback = callback;
            this.fallbackError = fallbackError;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (!response.isSuccessful()) {
                callback.onError(ApiErrorMapper.fromResponse(response, fallbackError));
                return;
            }

            if (isZeroRowsAffected(readBody(response.body()))) {
                callback.onError(fallbackError);
                return;
            }

            callback.onSuccess(null);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
            callback.onError(ApiErrorMapper.networkError());
        }
    }
}
