package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Prediction payload returned by the custom backend ML endpoint.
 */
public class PredictTaskCostResponse {

    @SerializedName("predictedCostAmount")
    private String predictedCostAmount;
    @SerializedName("modelVersion")
    private String modelVersion;
    @SerializedName("modelType")
    private String modelType;
    @SerializedName("generatedAt")
    private String generatedAt;

    public String getPredictedCostAmount() {
        return predictedCostAmount;
    }

    public void setPredictedCostAmount(String predictedCostAmount) {
        this.predictedCostAmount = predictedCostAmount;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}
