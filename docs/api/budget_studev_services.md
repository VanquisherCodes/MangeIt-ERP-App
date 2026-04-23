# Budget StudEV Services

## Update category allocation

**Name of service:** `UpdateBudgetCategoryAllocation`

**SQL query:**

```sql
UPDATE BudgetCategory bc
JOIN Budget b ON b.budget_id = bc.budget_id
JOIN GroupMembership gm ON gm.membership_id = (:createdbymembershipid)
LEFT JOIN (
    SELECT budget_id, SUM(allocated_amount) AS total_allocated
    FROM BudgetCategory
    GROUP BY budget_id
) budget_sums ON budget_sums.budget_id = bc.budget_id
LEFT JOIN (
    SELECT budget_category_id, SUM(allocated_amount) AS task_allocated
    FROM TaskBudget
    GROUP BY budget_category_id
) task_sums ON task_sums.budget_category_id = bc.budget_category_id
SET
    bc.allocated_amount = (:allocatedamount),
    bc.updated_at = CURRENT_TIMESTAMP
WHERE bc.budget_category_id = (:budgetcategoryid)
  AND gm.group_id = b.group_id
  AND gm.role_in_group = 'admin'
  AND gm.membership_status = 'active'
  AND (:allocatedamount) >= 0
  AND COALESCE(budget_sums.total_allocated, 0) - bc.allocated_amount + (:allocatedamount) <= b.total_amount
  AND (:allocatedamount) >= COALESCE(task_sums.task_allocated, 0);
```

**StudEV path order for Android:**

Because StudEV binds placeholders by occurrence order, the matching path is:

```text
UpdateBudgetCategoryAllocation/{createdbymembershipid}/{allocatedamount}/{budgetcategoryid}/{allocatedamount_check_1}/{allocatedamount_check_2}/{allocatedamount_check_3}
```

The Android app sends the same `allocatedamount` value three extra times to satisfy the repeated SQL placeholder occurrences.
