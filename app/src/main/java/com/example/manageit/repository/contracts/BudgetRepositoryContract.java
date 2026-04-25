package com.example.manageit.repository.contracts;

import com.example.manageit.repository.RepositoryCallback;

public interface BudgetRepositoryContract {
    void createBudget(
            String groupId,
            String name,
            String description,
            String totalAmount,
            String currencyCode,
            String periodStart,
            String periodEnd,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    );
}
