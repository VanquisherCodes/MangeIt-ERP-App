# ManageIt Backend

This module is the backend bridge between the Android app and Azure ML.

Architecture:

- Android app -> this backend -> Azure ML online endpoint
- Android app never calls Azure ML directly

## Endpoint

`POST /api/ml/predict-task-cost`

## Local Run

1. Put the Azure ML scoring URL and key in `src/main/resources/application.properties`.
2. Start the backend:

```bash
./gradlew :backend:bootRun
```

3. Point the Android app to the local backend through `local.properties`:

```properties
backendApiBaseUrl=http://10.0.2.2:8080/
```

`10.0.2.2` lets the Android emulator reach the host machine.
