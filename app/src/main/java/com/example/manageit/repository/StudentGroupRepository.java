package com.example.manageit.repository;

import com.example.manageit.models.StudentGroup;
import com.example.manageit.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Student groups fetched from StudEV services.
 */
public class StudentGroupRepository {

    private final ApiClient apiClient;

    public StudentGroupRepository() {
        this.apiClient = ApiClient.getInstance();
    }

    public void getAvailableGroups(RepositoryCallback<List<StudentGroup>> callback) {
        apiClient.getApiService().getAllGroups().enqueue(new Callback<List<StudentGroup>>() {
            @Override
            public void onResponse(Call<List<StudentGroup>> call, Response<List<StudentGroup>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Couldn't load student groups right now.");
                    return;
                }

                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<StudentGroup>> call, Throwable throwable) {
                callback.onError("Couldn't reach the server. Check your connection and try again.");
            }
        });
    }
}
