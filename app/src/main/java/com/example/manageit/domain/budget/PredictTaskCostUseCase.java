package com.example.manageit.domain.budget;

import com.example.manageit.models.PredictTaskCostRequest;
import com.example.manageit.models.PredictTaskCostResponse;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.repository.contracts.MlPredictionRepositoryContract;

public class PredictTaskCostUseCase {

    private final MlPredictionRepositoryContract mlPredictionRepository;

    public PredictTaskCostUseCase(MlPredictionRepositoryContract mlPredictionRepository) {
        this.mlPredictionRepository = mlPredictionRepository;
    }

    public void execute(
            PredictTaskCostRequest request,
            RepositoryCallback<PredictTaskCostResponse> callback
    ) {
        mlPredictionRepository.predictTaskCost(request, callback);
    }
}
