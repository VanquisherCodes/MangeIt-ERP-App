# ManageIt

ManageIt is an Android student-group management app backed by StudEV services. It supports registration/login, group enrollment, role-aware dashboards, tasks, announcements, events, budgeting, finance news, and optional ML task-cost prediction through a small backend bridge.

## Design Patterns Used

- **MVVM / Observer**: authentication uses `AuthViewModel` with `LiveData` so the UI observes loading, validation, success, and error states without owning login logic.
- **Repository pattern**: backend calls are isolated in repositories such as `AuthRepository`, `GroupMembershipRepository`, `BudgetRepository`, and `MarketauxNewsRepository`, keeping Activities/Fragments focused on UI behavior.
- **Use case layer**: flows such as login, registration, budget creation, admin access requests, and ML prediction are wrapped in use-case classes to separate business actions from data sources.
- **Dependency container**: `AppContainer` creates shared repositories/use cases in one place, which avoids scattering object construction through the UI.
- **Singleton API client**: `ApiClient` centralizes the StudEV Retrofit setup so all services use the same base URL and HTTP configuration.
- **Adapter/ViewHolder pattern**: list screens use adapters such as `GroupListAdapter`, `GroupMemberAdapter`, and `AdminAccessRequestAdapter` for efficient reusable row rendering.

## Test Login

There are no hard-coded demo credentials committed to the repository. For testing, use the app's **Register** screen to create a new account, then log in with that email and password. After login, request enrollment into a group; an admin account can approve pending enrollment requests from the membership management screen.

## Running Notes

- Open the project in Android Studio and run the `app` module, or build with `./gradlew :app:assembleDebug`.
- The Android app calls the StudEV API at `https://studev.groept.be/api/a25pt201/`.
- Finance news uses Marketaux. Add this to root `local.properties` if news should load:

```properties
marketauxApiToken=YOUR_MARKETAUX_TOKEN
```

- ML task-cost prediction is optional and uses the `backend` module as an Android-to-Azure-ML bridge. To run it locally, set `AZURE_ML_SCORING_URL` and `AZURE_ML_API_KEY`, start `./gradlew :backend:bootRun`, and add:

```properties
backendApiBaseUrl=http://10.0.2.2:8080/
```

- Main libraries are managed by Gradle: AndroidX AppCompat, Material Components, Retrofit/Gson, OkHttp logging, MPAndroidChart, and AndroidX Lifecycle.
