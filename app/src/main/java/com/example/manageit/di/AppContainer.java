package com.example.manageit.di;

import android.content.Context;

import com.example.manageit.domain.auth.LoginUseCase;
import com.example.manageit.domain.auth.RegisterUseCase;
import com.example.manageit.domain.budget.CreateBudgetUseCase;
import com.example.manageit.domain.budget.PredictTaskCostUseCase;
import com.example.manageit.domain.requests.RequestAdminAccessUseCase;
import com.example.manageit.network.ApiClient;
import com.example.manageit.repository.AdminAccessRequestRepository;
import com.example.manageit.repository.AuthRepository;
import com.example.manageit.repository.BudgetRepository;
import com.example.manageit.repository.MlPredictionRepository;
import com.example.manageit.repository.contracts.AuthRepositoryContract;
import com.example.manageit.repository.contracts.BudgetRepositoryContract;
import com.example.manageit.repository.contracts.MlPredictionRepositoryContract;
import com.example.manageit.viewmodel.AuthViewModelFactory;

public class AppContainer {

    private final Context appContext;
    private final ApiClient apiClient;
    private final AuthRepositoryContract authRepository;
    private final BudgetRepositoryContract budgetRepository;
    private final MlPredictionRepositoryContract mlPredictionRepository;
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final CreateBudgetUseCase createBudgetUseCase;
    private final PredictTaskCostUseCase predictTaskCostUseCase;

    public AppContainer(Context context) {
        this.appContext = context.getApplicationContext();
        this.apiClient = ApiClient.getInstance();
        this.authRepository = new AuthRepository(apiClient);
        this.budgetRepository = new BudgetRepository(apiClient);
        this.mlPredictionRepository = new MlPredictionRepository();
        this.loginUseCase = new LoginUseCase(authRepository);
        this.registerUseCase = new RegisterUseCase(authRepository);
        this.createBudgetUseCase = new CreateBudgetUseCase(budgetRepository);
        this.predictTaskCostUseCase = new PredictTaskCostUseCase(mlPredictionRepository);
    }

    public Context getAppContext() {
        return appContext;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public AuthRepositoryContract getAuthRepository() {
        return authRepository;
    }

    public BudgetRepositoryContract getBudgetRepository() {
        return budgetRepository;
    }

    public MlPredictionRepositoryContract getMlPredictionRepository() {
        return mlPredictionRepository;
    }

    public CreateBudgetUseCase getCreateBudgetUseCase() {
        return createBudgetUseCase;
    }

    public PredictTaskCostUseCase getPredictTaskCostUseCase() {
        return predictTaskCostUseCase;
    }

    public RequestAdminAccessUseCase createRequestAdminAccessUseCase() {
        return new RequestAdminAccessUseCase(new AdminAccessRequestRepository(apiClient));
    }

    public AuthViewModelFactory createAuthViewModelFactory() {
        return new AuthViewModelFactory(loginUseCase, registerUseCase);
    }
}
