package com.example.manageit.repository;

import com.example.manageit.models.Announcement;
import com.example.manageit.network.ApiClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Announcement operations scoped to one student group.
 */
public class GroupAnnouncementsRepository {

    private final ApiClient apiClient;

    public GroupAnnouncementsRepository() {
        this.apiClient = ApiClient.getInstance();
    }

    public void getGroupAnnouncements(String groupId, RepositoryCallback<List<Announcement>> callback) {
        apiClient.getApiService().getGroupAnnouncements(groupId).enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Couldn't load group announcements right now.");
                    return;
                }

                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable throwable) {
                callback.onError("Couldn't reach the server while loading announcements.");
            }
        });
    }

    public void createAnnouncement(
            String groupId,
            String title,
            String message,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .createAnnouncement(groupId, title, message, createdByMembershipId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            callback.onError("Couldn't create the group announcement.");
                            return;
                        }

                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while creating the announcement.");
                    }
                });
    }
}
