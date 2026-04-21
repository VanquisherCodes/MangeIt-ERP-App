package com.example.manageit.repository;

import com.example.manageit.models.Task;
import com.example.manageit.network.ApiClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Task operations scoped to one student group.
 */
public class GroupTasksRepository {

    private final ApiClient apiClient;

    public GroupTasksRepository() {
        this.apiClient = ApiClient.getInstance();
    }

    public void getGroupTasks(String groupId, RepositoryCallback<List<Task>> callback) {
        apiClient.getApiService().getGroupTasks(groupId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Couldn't load group tasks right now.");
                    return;
                }

                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable throwable) {
                callback.onError("Couldn't reach the server while loading tasks.");
            }
        });
    }

    public void createTask(
            String groupId,
            String title,
            String description,
            String dueDate,
            String assignedToMembershipId,
            String assignedByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .createTask(
                        groupId,
                        title,
                        description,
                        "pending",
                        dueDate,
                        assignedToMembershipId,
                        assignedByMembershipId
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            callback.onError("Couldn't create the group task.");
                            return;
                        }

                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while creating the task.");
                    }
                });
    }

    public void updateTaskStatus(String taskId, String status, RepositoryCallback<Void> callback) {
        apiClient.getApiService().updateTaskStatus(taskId, status).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    callback.onError("Couldn't update the task status.");
                    return;
                }

                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onError("Couldn't reach the server while updating the task status.");
            }
        });
    }
}
