package com.example.manageit.repository;

import com.example.manageit.apis.ml.MlBackendApiClient;
import com.example.manageit.apis.ml.MlBackendApiService;
import com.example.manageit.errors.ApiErrorMapper;
import com.example.manageit.models.PredictTaskCostRequest;
import com.example.manageit.models.PredictTaskCostResponse;
import com.example.manageit.repository.contracts.MlPredictionRepositoryContract;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Android-side wrapper for the custom backend ML prediction endpoint.
 */
public class MlPredictionRepository implements MlPredictionRepositoryContract {

    private final MlBackendApiService apiService;

    public MlPredictionRepository() {
        this(MlBackendApiClient.getService());
    }

    public MlPredictionRepository(MlBackendApiService apiService) {
        this.apiService = apiService;
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
                    callback.onError(ApiErrorMapper.fromResponse(
                            response,
                            "Couldn't get a task cost prediction from the backend."
                    ));
                    return;
                }

                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<PredictTaskCostResponse> call, Throwable throwable) {
                callback.onError(ApiErrorMapper.networkError());
            }
        });
    }
}
