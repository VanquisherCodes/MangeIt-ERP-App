package com.example.manageit.backend.service;

import com.example.manageit.backend.dto.PredictTaskCostRequest;
import com.example.manageit.backend.dto.PredictTaskCostResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AzureMlPredictionService {

    private final RestClient restClient;
    private final String scoringUrl;
    private final String apiKey;

    public AzureMlPredictionService(
            @Value("${azure.ml.scoring-url}") String scoringUrl,
            @Value("${azure.ml.api-key}") String apiKey
    ) {
        this.scoringUrl = scoringUrl;
        this.apiKey = apiKey;
        this.restClient = RestClient.builder().build();
    }

    public PredictTaskCostResponse predictTaskCost(PredictTaskCostRequest request) {
        validateConfiguration();

        Map<String, Object> azurePayload = new LinkedHashMap<>();
        azurePayload.put("groupId", parseWholeNumber(request.getGroupId(), "groupId"));
        azurePayload.put("categoryName", requireText(request.getCategoryName(), "categoryName"));
        azurePayload.put("priority", requireText(request.getPriority(), "priority"));
        azurePayload.put("estimatedHours", parseDecimal(request.getEstimatedHours(), "estimatedHours"));
        azurePayload.put("teamSize", parseWholeNumber(request.getTeamSize(), "teamSize"));
        azurePayload.put("allocatedAmount", parseDecimal(request.getAllocatedAmount(), "allocatedAmount"));

        AzureEndpointResponse azureResponse = restClient.post()
                .uri(scoringUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(azurePayload)
                .retrieve()
                .body(AzureEndpointResponse.class);

        if (azureResponse == null || azureResponse.getPredictedCostAmount() == null) {
            throw new IllegalStateException("Azure ML returned an empty prediction response.");
        }

        PredictTaskCostResponse response = new PredictTaskCostResponse();
        response.setPredictedCostAmount(azureResponse.getPredictedCostAmount().stripTrailingZeros().toPlainString());
        response.setModelVersion(
                azureResponse.getModelVersion() == null || azureResponse.getModelVersion().isBlank()
                        ? "v1"
                        : azureResponse.getModelVersion().trim()
        );
        response.setModelType(
                azureResponse.getModelType() == null || azureResponse.getModelType().isBlank()
                        ? "linear_regression"
                        : azureResponse.getModelType().trim()
        );
        response.setGeneratedAt(Instant.now().toString());
        return response;
    }

    private void validateConfiguration() {
        if (scoringUrl == null || scoringUrl.isBlank()) {
            throw new IllegalStateException("Missing azure.ml.scoring-url configuration.");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Missing azure.ml.api-key configuration.");
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required field: " + fieldName);
        }
        return value.trim();
    }

    private Integer parseWholeNumber(String value, String fieldName) {
        try {
            return Integer.valueOf(requireText(value, fieldName));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid whole number for " + fieldName, exception);
        }
    }

    private BigDecimal parseDecimal(String value, String fieldName) {
        try {
            return new BigDecimal(requireText(value, fieldName));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid decimal number for " + fieldName, exception);
        }
    }

    public static class AzureEndpointResponse {

        private BigDecimal predictedCostAmount;
        private String modelVersion;
        private String modelType;

        public BigDecimal getPredictedCostAmount() {
            return predictedCostAmount;
        }

        public void setPredictedCostAmount(BigDecimal predictedCostAmount) {
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
    }
}
