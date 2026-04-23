package com.example.manageit.repository;

import com.example.manageit.apis.ml.MlBackendApiClient;
import com.example.manageit.apis.ml.MlBackendApiService;
import com.example.manageit.models.PredictTaskCostRequest;
import com.example.manageit.models.PredictTaskCostResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Android-side wrapper for the custom backend ML prediction endpoint.
 */
public class MlPredictionRepository {

    private final MlBackendApiService apiService;

    public MlPredictionRepository() {
        this.apiService = MlBackendApiClient.getService();
    }

    public void predictTaskCost(
            PredictTaskCostRequest request,
            RepositoryCallback<PredictTaskCostResponse> callback
    ) {
        apiService.predictTaskCost(request).enqueue(new Callback<PredictTaskCostResponse>() {
            @Override
            public void onResponse(
                    Call<PredictTaskCostResponse> call,
                    Response<PredictTaskCostResponse> response
            ) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Couldn't get a task cost prediction from the backend.");
                    return;
                }

                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<PredictTaskCostResponse> call, Throwable throwable) {
                callback.onError("Couldn't reach the backend ML service.");
            }
        });
    }
}
