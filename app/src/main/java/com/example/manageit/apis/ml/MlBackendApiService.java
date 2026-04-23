package com.example.manageit.apis.ml;

import com.example.manageit.models.PredictTaskCostRequest;
import com.example.manageit.models.PredictTaskCostResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Backend endpoint used by Android. The backend then calls Azure ML.
 */
public interface MlBackendApiService {

    @POST("api/ml/predict-task-cost")
    Call<PredictTaskCostResponse> predictTaskCost(@Body PredictTaskCostRequest request);
}
