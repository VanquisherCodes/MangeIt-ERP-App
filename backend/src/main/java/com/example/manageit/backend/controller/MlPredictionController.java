package com.example.manageit.backend.controller;

import com.example.manageit.backend.dto.PredictTaskCostRequest;
import com.example.manageit.backend.dto.PredictTaskCostResponse;
import com.example.manageit.backend.service.AzureMlPredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ml")
public class MlPredictionController {

    private final AzureMlPredictionService azureMlPredictionService;

    public MlPredictionController(AzureMlPredictionService azureMlPredictionService) {
        this.azureMlPredictionService = azureMlPredictionService;
    }

    @PostMapping("/predict-task-cost")
    public ResponseEntity<PredictTaskCostResponse> predictTaskCost(
            @RequestBody PredictTaskCostRequest request
    ) {
        return ResponseEntity.ok(azureMlPredictionService.predictTaskCost(request));
    }
}
