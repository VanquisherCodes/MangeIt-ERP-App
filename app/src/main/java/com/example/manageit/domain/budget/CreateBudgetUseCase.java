package com.example.manageit.domain.budget;

import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.repository.contracts.BudgetRepositoryContract;

public class CreateBudgetUseCase {

    private final BudgetRepositoryContract budgetRepository;

    public CreateBudgetUseCase(BudgetRepositoryContract budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public void execute(
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
        budgetRepository.createBudget(
                groupId,
                name,
                description,
                totalAmount,
                currencyCode,
                periodStart,
                periodEnd,
                createdByMembershipId,
                callback
        );
    }
}
