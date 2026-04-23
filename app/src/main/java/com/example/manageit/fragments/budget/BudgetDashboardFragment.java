package com.example.manageit.fragments.budget;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.manageit.R;
import com.example.manageit.managers.SessionManager;
import com.example.manageit.models.Budget;
import com.example.manageit.models.BudgetCategory;
import com.example.manageit.models.BudgetDashboardData;
import com.example.manageit.models.BudgetOverview;
import com.example.manageit.models.Expense;
import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.PredictTaskCostRequest;
import com.example.manageit.models.PredictTaskCostResponse;
import com.example.manageit.models.Role;
import com.example.manageit.models.Task;
import com.example.manageit.models.TaskBudget;
import com.example.manageit.models.User;
import com.example.manageit.repository.BudgetRepository;
import com.example.manageit.repository.GroupMembershipRepository;
import com.example.manageit.repository.GroupTasksRepository;
import com.example.manageit.repository.MlPredictionRepository;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.utils.GreetingUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Budget dashboard shown to both admins and standard group members.
 */
public class BudgetDashboardFragment extends Fragment {

    private static final String ARG_GROUP_ID = "arg_group_id";
    private static final String ARG_GROUP_NAME = "arg_group_name";
    private static final String ARG_GROUP_ROLE = "arg_group_role";
    private static final String STUDEV_SPACE_TOKEN = "__sp__";

    private final BudgetRepository budgetRepository = new BudgetRepository();
    private final GroupMembershipRepository groupMembershipRepository = new GroupMembershipRepository();
    private final GroupTasksRepository groupTasksRepository = new GroupTasksRepository();
    private final MlPredictionRepository mlPredictionRepository = new MlPredictionRepository();

    private String groupId = "";
    private String groupName = "Student Group";
    private Role role = Role.USER;
    private @Nullable GroupMembership currentMembership;
    private BudgetDashboardData currentDashboardData = new BudgetDashboardData();

    public BudgetDashboardFragment() {
        super(R.layout.fragment_budget_dashboard);
    }

    public static BudgetDashboardFragment newInstance(String groupId, String groupName, String groupRole) {
        BudgetDashboardFragment fragment = new BudgetDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        args.putString(ARG_GROUP_NAME, groupName);
        args.putString(ARG_GROUP_ROLE, groupRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        groupId = args != null ? args.getString(ARG_GROUP_ID, "") : "";
        groupName = args != null ? args.getString(ARG_GROUP_NAME, "Student Group") : "Student Group";
        role = Role.from(args != null ? args.getString(ARG_GROUP_ROLE) : null);
        User currentUser = new SessionManager(requireContext()).getCurrentUser();

        ((TextView) view.findViewById(R.id.tv_budget_group_label)).setText(groupName);
        ((TextView) view.findViewById(R.id.tv_budget_greeting)).setText(GreetingUtils.getGreetingForCurrentTime());
        ((TextView) view.findViewById(R.id.tv_budget_name)).setText(GreetingUtils.getDisplayFirstName(currentUser));
        ((TextView) view.findViewById(R.id.tv_budget_membership_role))
                .setText("Group role: " + (role == Role.ADMIN ? "Admin" : "User"));
        ((TextView) view.findViewById(R.id.tv_budget_avatar_initial)).setText(GreetingUtils.getInitials(currentUser));
        ((TextView) view.findViewById(R.id.tv_budget_screen_hint)).setText(
                role == Role.ADMIN
                        ? "Create budgets, add categories, allocate tasks, and record actual costs."
                        : "You can review planned and actual spending for this group."
        );

        bindAdminActions(view);

        Button retryButton = view.findViewById(R.id.btn_retry_budget_dashboard);
        retryButton.setOnClickListener(v -> loadBudgetDashboard(view));

        resolveCurrentMembership(view);
        loadBudgetDashboard(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isAdded() || getView() == null) {
            return;
        }
        resolveCurrentMembership(requireView());
        loadBudgetDashboard(requireView());
    }

    private void bindAdminActions(View root) {
        Button createBudgetButton = root.findViewById(R.id.btn_budget_create);
        Button addCategoryButton = root.findViewById(R.id.btn_budget_add_category);
        Button allocateTaskButton = root.findViewById(R.id.btn_budget_allocate_task);

        createBudgetButton.setOnClickListener(v -> showCreateBudgetDialog());
        addCategoryButton.setOnClickListener(v -> showCreateBudgetCategoryDialog());
        allocateTaskButton.setOnClickListener(v -> showAllocateTaskBudgetDialog());

        updateAdminActionState(root);
    }

    private void resolveCurrentMembership(View root) {
        if (role != Role.ADMIN) {
            updateAdminActionState(root);
            return;
        }

        String userId = new SessionManager(requireContext()).getUserId();
        groupMembershipRepository.getMembership(userId, groupId, new RepositoryCallback<GroupMembership>() {
            @Override
            public void onSuccess(GroupMembership result) {
                currentMembership = result;
                if (!isAdded()) {
                    return;
                }
                updateAdminActionState(root);
            }

            @Override
            public void onError(String message) {
                currentMembership = null;
                if (!isAdded()) {
                    return;
                }
                updateAdminActionState(root);
            }
        });
    }

    private void updateAdminActionState(View root) {
        LinearLayout actionsLayout = root.findViewById(R.id.layout_budget_admin_actions);
        TextView noteView = root.findViewById(R.id.tv_budget_admin_note);
        Button createBudgetButton = root.findViewById(R.id.btn_budget_create);
        Button addCategoryButton = root.findViewById(R.id.btn_budget_add_category);
        Button allocateTaskButton = root.findViewById(R.id.btn_budget_allocate_task);

        if (role != Role.ADMIN) {
            actionsLayout.setVisibility(View.GONE);
            return;
        }

        actionsLayout.setVisibility(View.VISIBLE);
        boolean membershipReady = currentMembership != null
                && currentMembership.getMembershipId() != null
                && !currentMembership.getMembershipId().trim().isEmpty();
        boolean hasActiveBudget = getActiveBudgetId() != null;
        boolean hasCategories = currentDashboardData.getCategories() != null && !currentDashboardData.getCategories().isEmpty();

        createBudgetButton.setEnabled(membershipReady && !hasActiveBudget);
        addCategoryButton.setEnabled(membershipReady && hasActiveBudget);
        allocateTaskButton.setEnabled(membershipReady && hasActiveBudget && hasCategories);

        if (!membershipReady) {
            createBudgetButton.setText("Loading Admin Membership...");
            noteView.setText("Your admin membership is still loading. Budget actions will unlock shortly.");
        } else if (!hasActiveBudget) {
            createBudgetButton.setText("Create Active Budget");
            noteView.setText("Create an active budget first, then add categories and allocate tasks.");
        } else if (!hasCategories) {
            createBudgetButton.setText("Active Budget Already Exists");
            noteView.setText("Add at least one category before allocating budget to tasks.");
        } else {
            createBudgetButton.setText("Active Budget Already Exists");
            noteView.setText("Admin actions are ready. Task cards below also let you edit allocations and actual costs.");
        }
    }

    private void loadBudgetDashboard(View root) {
        ProgressBar progressBar = root.findViewById(R.id.progress_budget_dashboard);
        TextView messageView = root.findViewById(R.id.tv_budget_dashboard_message);
        Button retryButton = root.findViewById(R.id.btn_retry_budget_dashboard);
        LinearLayout contentLayout = root.findViewById(R.id.layout_budget_dashboard_content);

        progressBar.setVisibility(View.VISIBLE);
        messageView.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);

        budgetRepository.getBudgetDashboardData(groupId, new RepositoryCallback<BudgetDashboardData>() {
            @Override
            public void onSuccess(BudgetDashboardData result) {
                if (!isAdded()) {
                    return;
                }

                currentDashboardData = result == null ? new BudgetDashboardData() : result;
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                bindOverview(root, currentDashboardData.getOverview());
                bindMlPredictionPanel(root, currentDashboardData.getTaskBudgets());
                bindCategories(root, currentDashboardData.getCategories());
                bindTaskBudgets(root, currentDashboardData.getTaskBudgets());
                updateAdminActionState(root);

                if (getActiveBudgetId() == null) {
                    messageView.setText("No active budget has been created for this group yet.");
                    messageView.setVisibility(View.VISIBLE);
                } else {
                    messageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }

                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.GONE);
                messageView.setText(message);
                messageView.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void bindOverview(View root, @Nullable BudgetOverview overview) {
        TextView budgetName = root.findViewById(R.id.tv_budget_title);
        TextView budgetPeriod = root.findViewById(R.id.tv_budget_period);
        TextView totalBudget = root.findViewById(R.id.tv_budget_total_value);
        TextView totalAllocated = root.findViewById(R.id.tv_budget_allocated_value);
        TextView totalSpent = root.findViewById(R.id.tv_budget_spent_value);
        TextView totalRemaining = root.findViewById(R.id.tv_budget_remaining_value);
        PieChart pieChart = root.findViewById(R.id.chart_budget_overview);

        if (overview == null) {
            budgetName.setText("No active budget");
            budgetPeriod.setText("Create and activate a budget to see analytics.");
            totalBudget.setText("--");
            totalAllocated.setText("--");
            totalSpent.setText("--");
            totalRemaining.setText("--");
            clearBudgetPieChart(pieChart);
            return;
        }

        budgetName.setText(overview.getName() == null || overview.getName().trim().isEmpty()
                ? "Active Budget"
                : overview.getName().trim());
        budgetPeriod.setText(buildPeriodLabel(overview.getPeriodStart(), overview.getPeriodEnd(), overview.getStatus()));
        totalBudget.setText(formatMoney(overview.getTotalBudget(), overview.getCurrencyCode()));
        totalAllocated.setText(formatMoney(overview.getTotalAllocated(), overview.getCurrencyCode()));
        totalSpent.setText(formatMoney(overview.getTotalSpent(), overview.getCurrencyCode()));
        totalRemaining.setText(formatMoney(overview.getRemainingBudget(), overview.getCurrencyCode()));
        bindBudgetPieChart(pieChart, overview);
    }

    private void bindMlPredictionPanel(View root, List<TaskBudget> taskBudgets) {
        LinearLayout panel = root.findViewById(R.id.layout_budget_ml_prediction);
        TextView hintView = root.findViewById(R.id.tv_budget_ml_prediction_hint);
        Spinner taskSpinner = root.findViewById(R.id.spinner_budget_ml_task);
        EditText amountInput = root.findViewById(R.id.et_budget_ml_allocated_amount);
        Button predictButton = root.findViewById(R.id.btn_budget_ml_predict);
        TextView resultView = root.findViewById(R.id.tv_budget_ml_prediction_result);
        TextView metaView = root.findViewById(R.id.tv_budget_ml_prediction_meta);

        if (role != Role.ADMIN) {
            panel.setVisibility(View.GONE);
            return;
        }

        panel.setVisibility(View.VISIBLE);
        resultView.setVisibility(View.GONE);
        metaView.setVisibility(View.GONE);

        if (taskBudgets == null || taskBudgets.isEmpty()) {
            hintView.setText("Create at least one task budget to use ML prediction.");
            taskSpinner.setEnabled(false);
            amountInput.setEnabled(false);
            predictButton.setEnabled(false);
            amountInput.setText("");
            return;
        }

        hintView.setText("Select a task budget and ask the backend for a predicted cost.");
        taskSpinner.setEnabled(true);
        amountInput.setEnabled(true);
        predictButton.setEnabled(true);

        List<String> taskLabels = new ArrayList<>();
        for (TaskBudget taskBudget : taskBudgets) {
            taskLabels.add(
                    safeLabel(taskBudget.getTaskName(), "Task")
                            + " • "
                            + safeLabel(taskBudget.getCategoryName(), "Category")
            );
        }
        bindSimpleSpinner(taskSpinner, taskLabels);

        AdapterView.OnItemSelectedListener selectionListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TaskBudget selectedTaskBudget = taskBudgets.get(Math.max(0, position));
                amountInput.setText(
                        isBlankOrNullToken(selectedTaskBudget.getAllocatedAmount())
                                ? ""
                                : selectedTaskBudget.getAllocatedAmount().trim()
                );

                if (isBlankOrNullToken(selectedTaskBudget.getPredictedCostAmount())) {
                    resultView.setVisibility(View.GONE);
                    metaView.setVisibility(View.GONE);
                    return;
                }

                resultView.setText(
                        "Stored prediction: "
                                + formatMoney(selectedTaskBudget.getPredictedCostAmount(), getDefaultCurrencyCode())
                );
                resultView.setVisibility(View.VISIBLE);
                metaView.setText(
                        "Model: "
                                + safeLabel(selectedTaskBudget.getMlModelVersion(), "v1")
                                + " • Status: "
                                + safeLabel(selectedTaskBudget.getMlPredictionStatus(), "PREDICTED")
                );
                metaView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                amountInput.setText("");
            }
        };
        taskSpinner.setOnItemSelectedListener(selectionListener);
        taskSpinner.post(() -> selectionListener.onItemSelected(taskSpinner, null, taskSpinner.getSelectedItemPosition(), 0));

        predictButton.setOnClickListener(v -> {
            String amount = amountInput.getText().toString().trim();
            if (TextUtils.isEmpty(amount)) {
                amountInput.setError("Enter the amount to evaluate.");
                return;
            }

            TaskBudget selectedTaskBudget = taskBudgets.get(Math.max(0, taskSpinner.getSelectedItemPosition()));
            PredictTaskCostRequest request = new PredictTaskCostRequest(
                    groupId,
                    safeLabel(selectedTaskBudget.getCategoryName(), "miscellaneous"),
                    safeLabel(selectedTaskBudget.getPriority(), "MEDIUM").toUpperCase(Locale.getDefault()),
                    emptyToZero(selectedTaskBudget.getEstimatedHours()),
                    emptyToDefaultWholeNumber(selectedTaskBudget.getTeamSize(), "1"),
                    amount
            );

            predictButton.setEnabled(false);
            predictButton.setText("Predicting...");

            mlPredictionRepository.predictTaskCost(request, new RepositoryCallback<PredictTaskCostResponse>() {
                @Override
                public void onSuccess(PredictTaskCostResponse result) {
                    if (!isAdded()) {
                        return;
                    }

                    String predictedAmount = result == null ? null : result.getPredictedCostAmount();
                    if (isBlankOrNullToken(predictedAmount)) {
                        Toast.makeText(requireContext(), "Prediction response was empty.", Toast.LENGTH_LONG).show();
                    } else {
                        resultView.setText("Predicted cost: " + formatMoney(predictedAmount, getDefaultCurrencyCode()));
                        resultView.setVisibility(View.VISIBLE);
                        metaView.setText(
                                "Model: "
                                        + safeLabel(result.getModelType(), "linear_regression")
                                        + " • Version: "
                                        + safeLabel(result.getModelVersion(), "v1")
                        );
                        metaView.setVisibility(View.VISIBLE);
                    }

                    predictButton.setEnabled(true);
                    predictButton.setText("Predict Cost");
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) {
                        return;
                    }

                    predictButton.setEnabled(true);
                    predictButton.setText("Predict Cost");
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void bindCategories(View root, List<BudgetCategory> categories) {
        LinearLayout container = root.findViewById(R.id.layout_budget_categories);
        TextView emptyView = root.findViewById(R.id.tv_budget_categories_empty);

        container.removeAllViews();
        if (categories == null || categories.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        emptyView.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (BudgetCategory category : categories) {
            View itemView = inflater.inflate(R.layout.item_budget_category_summary, container, false);
            ((TextView) itemView.findViewById(R.id.tv_budget_category_name)).setText(safeLabel(category.getCategoryName(), "Category"));
            ((TextView) itemView.findViewById(R.id.tv_budget_category_allocated)).setText(
                    "Allocated: " + formatMoney(category.getAllocatedAmount(), getDefaultCurrencyCode())
            );
            ((TextView) itemView.findViewById(R.id.tv_budget_category_spent)).setText(
                    "Spent: " + formatMoney(category.getSpentAmount(), getDefaultCurrencyCode())
            );
            ((TextView) itemView.findViewById(R.id.tv_budget_category_remaining)).setText(
                    "Remaining: " + formatMoney(category.getRemainingToSpend(), getDefaultCurrencyCode())
            );
            ((ProgressBar) itemView.findViewById(R.id.progress_budget_category))
                    .setProgress(calculatePercent(category.getSpentAmount(), category.getAllocatedAmount()));
            Button editCategoryButton = itemView.findViewById(R.id.btn_edit_budget_category);
            if (role == Role.ADMIN) {
                editCategoryButton.setVisibility(View.VISIBLE);
                editCategoryButton.setOnClickListener(v -> showEditBudgetCategoryDialog(category));
            } else {
                editCategoryButton.setVisibility(View.GONE);
            }
            container.addView(itemView);
        }
    }

    private void bindTaskBudgets(View root, List<TaskBudget> taskBudgets) {
        LinearLayout container = root.findViewById(R.id.layout_budget_tasks);
        TextView emptyView = root.findViewById(R.id.tv_budget_tasks_empty);

        container.removeAllViews();
        if (taskBudgets == null || taskBudgets.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        emptyView.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        int displayCount = Math.min(taskBudgets.size(), 6);
        for (int i = 0; i < displayCount; i++) {
            TaskBudget taskBudget = taskBudgets.get(i);
            View itemView = inflater.inflate(R.layout.item_task_budget_summary, container, false);
            ((TextView) itemView.findViewById(R.id.tv_task_budget_name)).setText(safeLabel(taskBudget.getTaskName(), "Task"));
            ((TextView) itemView.findViewById(R.id.tv_task_budget_meta)).setText(
                    safeLabel(taskBudget.getCategoryName(), "Uncategorized")
                            + " • Priority " + safeLabel(taskBudget.getPriority(), "N/A")
            );
            ((TextView) itemView.findViewById(R.id.tv_task_budget_allocated)).setText(
                    "Allocated: " + formatMoney(taskBudget.getAllocatedAmount(), getDefaultCurrencyCode())
            );
            ((TextView) itemView.findViewById(R.id.tv_task_budget_predicted)).setText(
                    "Predicted: " + formatMoney(taskBudget.getPredictedCostAmount(), getDefaultCurrencyCode())
            );
            ((TextView) itemView.findViewById(R.id.tv_task_budget_actual)).setText(
                    "Actual: " + formatMoney(taskBudget.getActualCostAmount(), getDefaultCurrencyCode())
            );
            ((TextView) itemView.findViewById(R.id.tv_task_budget_variance)).setText(
                    buildVarianceLabel(taskBudget.getVarianceFromAllocated())
            );

            Button editAllocationButton = itemView.findViewById(R.id.btn_edit_task_budget_allocation);
            Button updateActualButton = itemView.findViewById(R.id.btn_update_task_budget_actual);
            Button addExpenseButton = itemView.findViewById(R.id.btn_add_task_budget_expense);
            Button viewExpensesButton = itemView.findViewById(R.id.btn_view_task_budget_expenses);
            if (role == Role.ADMIN) {
                editAllocationButton.setVisibility(View.VISIBLE);
                updateActualButton.setVisibility(View.VISIBLE);
                addExpenseButton.setVisibility(View.VISIBLE);
                editAllocationButton.setOnClickListener(v -> showTaskBudgetAmountDialog(taskBudget, true));
                updateActualButton.setOnClickListener(v -> showTaskBudgetAmountDialog(taskBudget, false));
                addExpenseButton.setOnClickListener(v -> showCreateExpenseDialog(taskBudget));
            } else {
                editAllocationButton.setVisibility(View.GONE);
                updateActualButton.setVisibility(View.GONE);
                addExpenseButton.setVisibility(View.GONE);
            }
            viewExpensesButton.setOnClickListener(v -> showTaskBudgetExpensesDialog(taskBudget));

            container.addView(itemView);
        }
    }

    private void showCreateBudgetDialog() {
        String membershipId = requireAdminMembershipId();
        if (membershipId == null) {
            return;
        }

        if (getActiveBudgetId() != null) {
            Toast.makeText(requireContext(), "This group already has an active budget.", Toast.LENGTH_LONG).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_budget, null, false);
        EditText nameInput = dialogView.findViewById(R.id.et_budget_name);
        EditText descriptionInput = dialogView.findViewById(R.id.et_budget_description);
        EditText totalInput = dialogView.findViewById(R.id.et_budget_total_amount);
        EditText startInput = dialogView.findViewById(R.id.et_budget_period_start);
        EditText endInput = dialogView.findViewById(R.id.et_budget_period_end);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Create Group Budget")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Create", null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(unused -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String name = nameInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();
                    String totalAmount = totalInput.getText().toString().trim();
                    String periodStart = startInput.getText().toString().trim();
                    String periodEnd = endInput.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        nameInput.setError("Enter a budget name.");
                        return;
                    }
                    if (TextUtils.isEmpty(totalAmount)) {
                        totalInput.setError("Enter the total budget amount.");
                        return;
                    }
                    if (TextUtils.isEmpty(periodStart)) {
                        startInput.setError("Enter the start date.");
                        return;
                    }
                    if (TextUtils.isEmpty(periodEnd)) {
                        endInput.setError("Enter the end date.");
                        return;
                    }

                    budgetRepository.createBudget(
                            groupId,
                            name,
                            description,
                            totalAmount,
                            "EUR",
                            periodStart,
                            periodEnd,
                            membershipId,
                            new RepositoryCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Budget created.", Toast.LENGTH_SHORT).show();
                                    loadBudgetDashboard(requireView());
                                }

                                @Override
                                public void onError(String message) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }));
        dialog.show();
    }

    private void showCreateBudgetCategoryDialog() {
        String membershipId = requireAdminMembershipId();
        String budgetId = getActiveBudgetId();
        if (membershipId == null || budgetId == null) {
            Toast.makeText(requireContext(), "Create an active budget first.", Toast.LENGTH_LONG).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_budget_category, null, false);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_budget_category_name);
        EditText amountInput = dialogView.findViewById(R.id.et_budget_category_amount);
        EditText notesInput = dialogView.findViewById(R.id.et_budget_category_notes);

        List<String> categoryOptions = new ArrayList<>();
        categoryOptions.add("equipment");
        categoryOptions.add("events");
        categoryOptions.add("travel");
        categoryOptions.add("marketing");
        categoryOptions.add("miscellaneous");
        bindSimpleSpinner(categorySpinner, categoryOptions);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Budget Category")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Create", null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(unused -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String categoryName = categoryOptions.get(Math.max(0, categorySpinner.getSelectedItemPosition()));
                    String amount = amountInput.getText().toString().trim();
                    String notes = notesInput.getText().toString().trim();

                    if (TextUtils.isEmpty(amount)) {
                        amountInput.setError("Enter the allocated amount.");
                        return;
                    }

                    budgetRepository.createBudgetCategory(
                            budgetId,
                            categoryName,
                            amount,
                            notes,
                            String.valueOf(currentDashboardData.getCategories().size() + 1),
                            membershipId,
                            new RepositoryCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Budget category added.", Toast.LENGTH_SHORT).show();
                                    loadBudgetDashboard(requireView());
                                }

                                @Override
                                public void onError(String message) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }));
        dialog.show();
    }

    private void showAllocateTaskBudgetDialog() {
        String membershipId = requireAdminMembershipId();
        String budgetId = getActiveBudgetId();
        if (membershipId == null || budgetId == null) {
            Toast.makeText(requireContext(), "Create an active budget first.", Toast.LENGTH_LONG).show();
            return;
        }
        if (currentDashboardData.getCategories().isEmpty()) {
            Toast.makeText(requireContext(), "Add a budget category first.", Toast.LENGTH_LONG).show();
            return;
        }

        groupTasksRepository.getGroupTasks(groupId, new RepositoryCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> result) {
                if (!isAdded()) {
                    return;
                }
                List<Task> availableTasks = filterAvailableTasks(result);
                if (availableTasks.isEmpty()) {
                    Toast.makeText(requireContext(), "All current tasks already have a budget allocation.", Toast.LENGTH_LONG).show();
                    return;
                }
                openAllocateTaskBudgetDialog(budgetId, membershipId, availableTasks);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openAllocateTaskBudgetDialog(String budgetId, String membershipId, List<Task> availableTasks) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_allocate_task_budget, null, false);
        Spinner taskSpinner = dialogView.findViewById(R.id.spinner_allocate_task);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_allocate_category);
        EditText amountInput = dialogView.findViewById(R.id.et_allocate_amount);
        Button predictButton = dialogView.findViewById(R.id.btn_allocate_predict_cost);
        TextView predictionResultView = dialogView.findViewById(R.id.tv_allocate_prediction_result);
        TextView predictionMetaView = dialogView.findViewById(R.id.tv_allocate_prediction_meta);

        List<String> taskLabels = new ArrayList<>();
        for (Task task : availableTasks) {
            taskLabels.add(task.getTitle() + " • " + safeLabel(task.getPriority(), "MEDIUM"));
        }
        bindSimpleSpinner(taskSpinner, taskLabels);

        List<String> categoryLabels = new ArrayList<>();
        for (BudgetCategory category : currentDashboardData.getCategories()) {
            categoryLabels.add(safeLabel(category.getCategoryName(), "Category") + " • Remaining "
                    + formatMoney(category.getRemainingToAllocate(), getDefaultCurrencyCode()));
        }
        bindSimpleSpinner(categorySpinner, categoryLabels);

        predictButton.setOnClickListener(v -> {
            String amount = amountInput.getText().toString().trim();
            if (TextUtils.isEmpty(amount)) {
                amountInput.setError("Enter the allocation amount before predicting.");
                return;
            }

            Task selectedTask = availableTasks.get(Math.max(0, taskSpinner.getSelectedItemPosition()));
            BudgetCategory selectedCategory = currentDashboardData.getCategories()
                    .get(Math.max(0, categorySpinner.getSelectedItemPosition()));

            PredictTaskCostRequest request = new PredictTaskCostRequest(
                    groupId,
                    safeLabel(selectedCategory.getCategoryName(), "miscellaneous"),
                    safeLabel(selectedTask.getPriority(), "MEDIUM").toUpperCase(Locale.getDefault()),
                    emptyToZero(selectedTask.getEstimatedHours()),
                    emptyToDefaultWholeNumber(selectedTask.getTeamSize(), "1"),
                    amount
            );

            predictionResultView.setVisibility(View.GONE);
            predictionMetaView.setVisibility(View.GONE);
            predictButton.setEnabled(false);
            predictButton.setText("Predicting...");

            mlPredictionRepository.predictTaskCost(request, new RepositoryCallback<PredictTaskCostResponse>() {
                @Override
                public void onSuccess(PredictTaskCostResponse result) {
                    if (!isAdded()) {
                        return;
                    }

                    String predictedAmount = result == null ? null : result.getPredictedCostAmount();
                    if (predictedAmount == null || predictedAmount.trim().isEmpty()) {
                        Toast.makeText(requireContext(), "Prediction response was empty.", Toast.LENGTH_LONG).show();
                    } else {
                        predictionResultView.setText(
                                "Predicted cost: " + formatMoney(predictedAmount, getDefaultCurrencyCode())
                        );
                        predictionResultView.setVisibility(View.VISIBLE);
                        predictionMetaView.setText(
                                "Model: "
                                        + safeLabel(result.getModelType(), "linear_regression")
                                        + " • Version: "
                                        + safeLabel(result.getModelVersion(), "v1")
                        );
                        predictionMetaView.setVisibility(View.VISIBLE);
                    }

                    predictButton.setEnabled(true);
                    predictButton.setText("Predict Cost");
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) {
                        return;
                    }

                    predictButton.setEnabled(true);
                    predictButton.setText("Predict Cost");
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        });

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Allocate Budget To Task")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Allocate", null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(unused -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String amount = amountInput.getText().toString().trim();
                    if (TextUtils.isEmpty(amount)) {
                        amountInput.setError("Enter the allocation amount.");
                        return;
                    }

                    Task selectedTask = availableTasks.get(Math.max(0, taskSpinner.getSelectedItemPosition()));
                    BudgetCategory selectedCategory = currentDashboardData.getCategories()
                            .get(Math.max(0, categorySpinner.getSelectedItemPosition()));

                    budgetRepository.createTaskBudget(
                            budgetId,
                            selectedCategory.getBudgetCategoryId(),
                            selectedTask.getId(),
                            amount,
                            membershipId,
                            new RepositoryCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Task budget allocated.", Toast.LENGTH_SHORT).show();
                                    loadBudgetDashboard(requireView());
                                }

                                @Override
                                public void onError(String message) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }));
        dialog.show();
    }

    private void showTaskBudgetAmountDialog(TaskBudget taskBudget, boolean editAllocation) {
        String membershipId = requireAdminMembershipId();
        if (membershipId == null) {
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_task_budget_amount, null, false);
        TextView subtitleView = dialogView.findViewById(R.id.tv_task_budget_amount_subtitle);
        EditText amountInput = dialogView.findViewById(R.id.et_task_budget_amount);

        String existingAmount = editAllocation ? taskBudget.getAllocatedAmount() : taskBudget.getActualCostAmount();
        subtitleView.setText(taskBudget.getTaskName() + " • " + safeLabel(taskBudget.getCategoryName(), "Category"));
        amountInput.setText(existingAmount == null ? "" : existingAmount);

        String dialogTitle = editAllocation ? "Edit Task Allocation" : "Update Actual Cost";
        String successMessage = editAllocation ? "Task allocation updated." : "Actual cost updated.";

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(unused -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String amount = amountInput.getText().toString().trim();
                    if (TextUtils.isEmpty(amount)) {
                        amountInput.setError("Enter an amount.");
                        return;
                    }

                    RepositoryCallback<Void> callback = new RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            if (!isAdded()) {
                                return;
                            }
                            dialog.dismiss();
                            Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();
                            loadBudgetDashboard(requireView());
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) {
                                return;
                            }
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                        }
                    };

                    if (editAllocation) {
                        budgetRepository.updateTaskBudgetAllocation(
                                taskBudget.getTaskBudgetId(),
                                amount,
                                membershipId,
                                callback
                        );
                    } else {
                        budgetRepository.updateTaskBudgetActualCost(
                                taskBudget.getTaskBudgetId(),
                                amount,
                                membershipId,
                                callback
                        );
                    }
                }));
        dialog.show();
    }

    private void showEditBudgetCategoryDialog(BudgetCategory category) {
        String membershipId = requireAdminMembershipId();
        if (membershipId == null) {
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_task_budget_amount, null, false);
        TextView subtitleView = dialogView.findViewById(R.id.tv_task_budget_amount_subtitle);
        EditText amountInput = dialogView.findViewById(R.id.et_task_budget_amount);

        subtitleView.setText(
                safeLabel(category.getCategoryName(), "Category")
                        + " • Allocated to tasks: "
                        + formatMoney(category.getAllocatedToTasks(), getDefaultCurrencyCode())
        );
        amountInput.setHint("New category allocation");
        amountInput.setText(category.getAllocatedAmount() == null ? "" : category.getAllocatedAmount());

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Category Allocation")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(unused -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String amount = amountInput.getText().toString().trim();
                    if (TextUtils.isEmpty(amount)) {
                        amountInput.setError("Enter an amount.");
                        return;
                    }

                    budgetRepository.updateBudgetCategoryAllocation(
                            category.getBudgetCategoryId(),
                            amount,
                            membershipId,
                            new RepositoryCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Category allocation updated.", Toast.LENGTH_SHORT).show();
                                    loadBudgetDashboard(requireView());
                                }

                                @Override
                                public void onError(String message) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }));
        dialog.show();
    }

    private void showCreateExpenseDialog(TaskBudget taskBudget) {
        String membershipId = requireAdminMembershipId();
        if (membershipId == null) {
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_expense, null, false);
        TextView subtitleView = dialogView.findViewById(R.id.tv_create_expense_subtitle);
        EditText titleInput = dialogView.findViewById(R.id.et_expense_title);
        EditText amountInput = dialogView.findViewById(R.id.et_expense_amount);
        EditText dateInput = dialogView.findViewById(R.id.et_expense_date);

        subtitleView.setText(taskBudget.getTaskName() + " • " + safeLabel(taskBudget.getCategoryName(), "Category"));
        dateInput.setText(LocalDate.now().toString());

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Expense")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(unused -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String title = titleInput.getText().toString().trim();
                    String amount = amountInput.getText().toString().trim();
                    String date = dateInput.getText().toString().trim();

                    if (TextUtils.isEmpty(title)) {
                        titleInput.setError("Enter an expense title.");
                        return;
                    }
                    if (TextUtils.isEmpty(amount)) {
                        amountInput.setError("Enter the expense amount.");
                        return;
                    }
                    if (TextUtils.isEmpty(date)) {
                        dateInput.setError("Enter the expense date.");
                        return;
                    }

                    budgetRepository.createExpense(
                            taskBudget.getTaskBudgetId(),
                            title,
                            amount,
                            date,
                            null,
                            null,
                            membershipId,
                            new RepositoryCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Expense added and actual cost synced.", Toast.LENGTH_SHORT).show();
                                    loadBudgetDashboard(requireView());
                                }

                                @Override
                                public void onError(String message) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }));
        dialog.show();
    }

    private void showTaskBudgetExpensesDialog(TaskBudget taskBudget) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_task_budget_expenses, null, false);
        TextView subtitleView = dialogView.findViewById(R.id.tv_task_budget_expenses_subtitle);
        TextView emptyView = dialogView.findViewById(R.id.tv_task_budget_expenses_empty);
        LinearLayout container = dialogView.findViewById(R.id.layout_task_budget_expenses);
        subtitleView.setText(taskBudget.getTaskName() + " • " + safeLabel(taskBudget.getCategoryName(), "Category"));

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Task Expenses")
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .create();

        dialog.show();

        budgetRepository.getTaskBudgetExpenses(taskBudget.getTaskBudgetId(), new RepositoryCallback<List<Expense>>() {
            @Override
            public void onSuccess(List<Expense> result) {
                if (!isAdded()) {
                    return;
                }
                bindExpensesIntoContainer(container, emptyView, result);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                container.removeAllViews();
                emptyView.setText(message);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void bindExpensesIntoContainer(LinearLayout container, TextView emptyView, @Nullable List<Expense> expenses) {
        container.removeAllViews();
        if (expenses == null || expenses.isEmpty()) {
            emptyView.setText("No expenses recorded yet.");
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        emptyView.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (Expense expense : expenses) {
            View itemView = inflater.inflate(R.layout.item_expense_summary, container, false);
            ((TextView) itemView.findViewById(R.id.tv_expense_title)).setText(safeLabel(expense.getExpenseTitle(), "Expense"));
            ((TextView) itemView.findViewById(R.id.tv_expense_amount)).setText(
                    formatMoney(expense.getExpenseAmount(), getDefaultCurrencyCode())
            );
            String vendor = isBlankOrNullToken(expense.getVendorName())
                    ? ""
                    : expense.getVendorName().trim();
            String dateLabel = safeLabel(expense.getExpenseDate(), "Unknown date");
            ((TextView) itemView.findViewById(R.id.tv_expense_date_vendor)).setText(
                    vendor.isEmpty() ? dateLabel : dateLabel + " • " + vendor
            );
            TextView notesView = itemView.findViewById(R.id.tv_expense_notes);
            if (isBlankOrNullToken(expense.getNotes())) {
                notesView.setVisibility(View.GONE);
            } else {
                notesView.setVisibility(View.VISIBLE);
                notesView.setText(expense.getNotes().trim());
            }
            container.addView(itemView);
        }
    }

    private List<Task> filterAvailableTasks(@Nullable List<Task> allTasks) {
        List<Task> availableTasks = new ArrayList<>();
        if (allTasks == null) {
            return availableTasks;
        }

        Set<String> allocatedTaskIds = new HashSet<>();
        for (TaskBudget taskBudget : currentDashboardData.getTaskBudgets()) {
            if (taskBudget != null && taskBudget.getTaskId() != null) {
                allocatedTaskIds.add(taskBudget.getTaskId().trim());
            }
        }

        for (Task task : allTasks) {
            if (task == null || task.getId() == null) {
                continue;
            }
            if (!allocatedTaskIds.contains(task.getId().trim())) {
                availableTasks.add(task);
            }
        }
        return availableTasks;
    }

    private void bindSimpleSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private @Nullable String requireAdminMembershipId() {
        if (role != Role.ADMIN) {
            Toast.makeText(requireContext(), "Only admins can change budget data.", Toast.LENGTH_LONG).show();
            return null;
        }
        if (currentMembership == null || currentMembership.getMembershipId() == null
                || currentMembership.getMembershipId().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Your admin membership is still loading.", Toast.LENGTH_LONG).show();
            return null;
        }
        return currentMembership.getMembershipId().trim();
    }

    private @Nullable String getActiveBudgetId() {
        Budget budget = currentDashboardData.getActiveBudget();
        if (budget != null && budget.getBudgetId() != null && !budget.getBudgetId().trim().isEmpty()) {
            return budget.getBudgetId().trim();
        }

        BudgetOverview overview = currentDashboardData.getOverview();
        if (overview != null && overview.getBudgetId() != null && !overview.getBudgetId().trim().isEmpty()) {
            return overview.getBudgetId().trim();
        }

        return null;
    }

    private void bindBudgetPieChart(PieChart pieChart, BudgetOverview overview) {
        double totalBudget = parseDouble(overview.getTotalBudget());
        double totalSpent = Math.max(0d, parseDouble(overview.getTotalSpent()));
        double totalAllocated = Math.max(0d, parseDouble(overview.getTotalAllocated()));
        double unallocated = Math.max(0d, parseDouble(overview.getUnallocatedBudget()));
        double reserved = Math.max(0d, totalAllocated - totalSpent);
        double unspentOpen = Math.max(0d, totalBudget - totalAllocated);
        if (unallocated > 0d) {
            unspentOpen = unallocated;
        }

        List<PieEntry> entries = new ArrayList<>();
        if (totalSpent > 0d) {
            entries.add(new PieEntry((float) totalSpent, "Spent"));
        }
        if (reserved > 0d) {
            entries.add(new PieEntry((float) reserved, "Allocated Left"));
        }
        if (unspentOpen > 0d) {
            entries.add(new PieEntry((float) unspentOpen, "Unallocated"));
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "No budget usage"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        ArrayList<Integer> colors = new ArrayList<>();
        if (entries.size() == 1 && "No budget usage".equals(entries.get(0).getLabel())) {
            colors.add(ContextCompat.getColor(requireContext(), R.color.surface_container_highest));
        } else {
            colors.add(ContextCompat.getColor(requireContext(), R.color.tertiary));
            colors.add(ContextCompat.getColor(requireContext(), R.color.primary));
            colors.add(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        }
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface));
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);

        pieChart.setUsePercentValues(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleRadius(62f);
        pieChart.setTransparentCircleRadius(66f);
        pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.surface_container));
        pieChart.setTransparentCircleColor(ContextCompat.getColor(requireContext(), R.color.surface_container_high));
        pieChart.setTransparentCircleAlpha(80);
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
        pieChart.setCenterText("Budget\nMix");
        pieChart.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface));
        pieChart.setCenterTextSize(16f);
        pieChart.setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.on_surface));
        pieChart.setData(pieData);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        pieChart.invalidate();
    }

    private void clearBudgetPieChart(PieChart pieChart) {
        pieChart.clear();
        pieChart.setNoDataText("No budget data yet");
        pieChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        pieChart.invalidate();
    }

    private String getDefaultCurrencyCode() {
        Budget activeBudget = currentDashboardData.getActiveBudget();
        if (activeBudget != null && activeBudget.getCurrencyCode() != null && !activeBudget.getCurrencyCode().trim().isEmpty()) {
            return activeBudget.getCurrencyCode().trim();
        }

        BudgetOverview overview = currentDashboardData.getOverview();
        if (overview != null && overview.getCurrencyCode() != null && !overview.getCurrencyCode().trim().isEmpty()) {
            return overview.getCurrencyCode().trim();
        }

        return "EUR";
    }

    private String buildPeriodLabel(String periodStart, String periodEnd, String status) {
        String start = safeLabel(periodStart, "Unknown start");
        String end = safeLabel(periodEnd, "Unknown end");
        String statusLabel = safeLabel(status, "draft").toUpperCase(Locale.getDefault());
        return start + " to " + end + " • " + statusLabel;
    }

    private String buildVarianceLabel(String variance) {
        double value = parseDouble(variance);
        if (Math.abs(value) < 0.01d) {
            return "Variance: On budget";
        }
        if (value > 0) {
            return "Variance: " + formatMoney(variance, getDefaultCurrencyCode()) + " over allocation";
        }
        return "Variance: " + formatMoney(String.valueOf(Math.abs(value)), getDefaultCurrencyCode()) + " under allocation";
    }

    private int calculatePercent(String numerator, String denominator) {
        double top = parseDouble(numerator);
        double bottom = parseDouble(denominator);
        if (bottom <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(100, (int) Math.round((top / bottom) * 100)));
    }

    private double parseDouble(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return 0d;
        }
        try {
            return Double.parseDouble(rawValue.trim());
        } catch (NumberFormatException ignored) {
            return 0d;
        }
    }

    private String formatMoney(String amount, String currencyCode) {
        if (amount == null || amount.trim().isEmpty()) {
            return "--";
        }
        String currency = currencyCode == null || currencyCode.trim().isEmpty() ? "EUR" : currencyCode.trim();
        try {
            return currency + " " + String.format(Locale.getDefault(), "%.2f", Double.parseDouble(amount.trim()));
        } catch (NumberFormatException ignored) {
            return currency + " " + amount.trim();
        }
    }

    private String safeLabel(String value, String fallback) {
        if (isBlankOrNullToken(value)) {
            return fallback;
        }
        return value.trim().replace(STUDEV_SPACE_TOKEN, " ");
    }

    private boolean isBlankOrNullToken(String value) {
        return value == null
                || value.trim().isEmpty()
                || "null".equalsIgnoreCase(value.trim());
    }

    private String emptyToZero(String value) {
        return isBlankOrNullToken(value) ? "0" : value.trim();
    }

    private String emptyToDefaultWholeNumber(String value, String fallback) {
        return isBlankOrNullToken(value) ? fallback : value.trim();
    }
}
