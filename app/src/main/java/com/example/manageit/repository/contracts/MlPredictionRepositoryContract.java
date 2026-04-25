package com.example.manageit.repository.contracts;

import com.example.manageit.models.PredictTaskCostRequest;
import com.example.manageit.models.PredictTaskCostResponse;
import com.example.manageit.repository.RepositoryCallback;

public interface MlPredictionRepositoryContract {
    void predictTaskCost(
            PredictTaskCostRequest request,
            RepositoryCallback<PredictTaskCostResponse> callback
    );
}
