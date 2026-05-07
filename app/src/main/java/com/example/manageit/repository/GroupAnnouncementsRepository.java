package com.example.manageit.repository;

import com.example.manageit.models.Announcement;
import com.example.manageit.network.ApiClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
                .createAnnouncement(
                        groupId,
                        encodePathValue(title),
                        encodePathValue(message),
                        createdByMembershipId
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String body = readResponseBody(response);
                        if (!response.isSuccessful() || looksLikeServerError(body)) {
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

    public void updateAnnouncement(
            String announcementId,
            String title,
            String message,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .updateAnnouncement(
                        encodePathValue(title),
                        encodePathValue(message),
                        announcementId
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String body = readResponseBody(response);
                        if (looksLikeMissingService(body)) {
                            callback.onError("Announcement edit API is not available yet.");
                            return;
                        }
                        if (!response.isSuccessful() || looksLikeServerError(body)) {
                            callback.onError("Couldn't update this announcement.");
                            return;
                        }

                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while updating the announcement.");
                    }
                });
    }

    public void deleteAnnouncement(String announcementId, RepositoryCallback<Void> callback) {
        apiClient.getApiService().deleteAnnouncement(announcementId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String body = readResponseBody(response);
                if (looksLikeMissingService(body)) {
                    callback.onError("Announcement delete API is not available yet.");
                    return;
                }
                if (!response.isSuccessful() || looksLikeServerError(body)) {
                    callback.onError("Couldn't delete this announcement.");
                    return;
                }

                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onError("Couldn't reach the server while deleting the announcement.");
            }
        });
    }

    private String encodePathValue(String rawValue) {
        String safeValue = rawValue == null ? "" : rawValue.trim();
        try {
            return URLEncoder.encode(safeValue, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            return safeValue.replace(" ", "+");
        }
    }

    private String readResponseBody(Response<ResponseBody> response) {
        if (response == null) {
            return "";
        }
        if (response.body() != null) {
            return readBody(response.body());
        }
        return readBody(response.errorBody());
    }

    private String readBody(ResponseBody body) {
        if (body == null) {
            return "";
        }

        try {
            return body.string();
        } catch (IOException ignored) {
            return "";
        }
    }

    private boolean looksLikeServerError(String body) {
        if (body == null) {
            return false;
        }

        String normalized = body.toLowerCase();
        return normalized.contains("fatal error")
                || normalized.contains("pdoexception")
                || normalized.contains("sqlstate");
    }

    private boolean looksLikeMissingService(String body) {
        return body != null && body.toLowerCase().contains("service not found");
    }
}
