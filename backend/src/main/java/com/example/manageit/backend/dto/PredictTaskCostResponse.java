package com.example.manageit.backend.dto;

public class PredictTaskCostResponse {

    private String predictedCostAmount;
    private String modelVersion;
    private String modelType;
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
