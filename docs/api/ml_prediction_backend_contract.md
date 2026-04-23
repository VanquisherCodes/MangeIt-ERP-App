# ML Prediction Backend Contract

This repository now contains a simple backend module at [`backend/`](/Users/kanishkk/AndroidStudioProjects/ManageIt/backend/).

The production architecture stays:

- Android app -> custom backend -> Azure ML online endpoint
- Android app never calls Azure ML directly

## Backend Endpoint

`POST /api/ml/predict-task-cost`

## Request Body

```json
{
  "groupId": "1",
  "categoryName": "equipment",
  "priority": "HIGH",
  "estimatedHours": "10",
  "teamSize": "3",
  "allocatedAmount": "350"
}
```

## Response Body

```json
{
  "predictedCostAmount": "347.86",
  "modelVersion": "v1",
  "modelType": "linear_regression",
  "generatedAt": "2026-04-24T00:00:00Z"
}
```

## Android Wiring Added In This Repo

- `BuildConfig.BACKEND_API_BASE_URL`
- `com.example.manageit.apis.ml.MlBackendApiClient`
- `com.example.manageit.apis.ml.MlBackendApiService`
- `com.example.manageit.models.PredictTaskCostRequest`
- `com.example.manageit.models.PredictTaskCostResponse`
- `com.example.manageit.repository.MlPredictionRepository`

## Backend Wiring Added In This Repo

- `backend/src/main/java/com/example/manageit/backend/controller/MlPredictionController.java`
- `backend/src/main/java/com/example/manageit/backend/service/AzureMlPredictionService.java`
- `backend/src/main/java/com/example/manageit/backend/dto/PredictTaskCostRequest.java`
- `backend/src/main/java/com/example/manageit/backend/dto/PredictTaskCostResponse.java`

## Local Development Base URL

The Android app reads the backend base URL from `local.properties`:

```properties
backendApiBaseUrl=http://10.0.2.2:8080/
```

`10.0.2.2` lets the Android emulator reach a backend running on the local machine.

For a deployed backend, replace the value with your real HTTPS base URL.
