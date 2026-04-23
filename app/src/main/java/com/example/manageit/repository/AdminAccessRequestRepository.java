package com.example.manageit.repository;

import android.content.Context;

import com.example.manageit.models.Request;
import com.example.manageit.network.ApiClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Backend-backed enrollment-request operations scoped to one student group.
 */
public class AdminAccessRequestRepository {

    private static final String ERROR_LOAD_GROUP = "Couldn't load enrollment requests right now.";
    private static final String ERROR_LOAD_USER = "Couldn't load your enrollment-request status right now.";
    private static final String ERROR_CREATE = "Couldn't submit your enrollment request right now.";
    private static final String ERROR_UPDATE = "Couldn't update this enrollment request right now.";
    private static final String DEFAULT_DESCRIPTION = "requested_user_enrollment";

    private final ApiClient apiClient;

    public AdminAccessRequestRepository(Context context) {
        this.apiClient = ApiClient.getInstance();
    }

    public void getGroupAdminAccessRequests(String groupId, RepositoryCallback<List<Request>> callback) {
        apiClient.getApiService().getGroupAdminAccessRequestsLegacy(groupId)
                .enqueue(new Callback<List<Request>>() {
                    @Override
                    public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            callback.onError(ERROR_LOAD_GROUP);
                            return;
                        }

                        callback.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Request>> call, Throwable throwable) {
                        callback.onError(ERROR_LOAD_GROUP);
                    }
                });
    }

    public void getUserAdminAccessRequest(
            String userId,
            String groupId,
            RepositoryCallback<Request> callback
    ) {
        apiClient.getApiService().getUserAdminAccessRequestLegacy(userId, groupId)
                .enqueue(new Callback<List<Request>>() {
                    @Override
                    public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                        if (!response.isSuccessful()) {
                            callback.onError(ERROR_LOAD_USER);
                            return;
                        }

                        List<Request> requests = response.body();
                        if (requests == null || requests.isEmpty()) {
                            callback.onSuccess(null);
                            return;
                        }

                        callback.onSuccess(requests.get(0));
                    }

                    @Override
                    public void onFailure(Call<List<Request>> call, Throwable throwable) {
                        callback.onError(ERROR_LOAD_USER);
                    }
                });
    }

    public void createAdminAccessRequest(
            String userId,
            String groupId,
            String description,
            String requesterFirstName,
            String requesterLastName,
            String requesterEmail,
            RepositoryCallback<Request> callback
    ) {
        String safeDescription = sanitizeDescription(description);
        apiClient.getApiService().createAdminAccessRequestLegacy(
                        userId,
                        groupId,
                        safeDescription,
                        userId,
                        groupId
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            callback.onError(ERROR_CREATE);
                            return;
                        }

                        getUserAdminAccessRequest(userId, groupId, new RepositoryCallback<Request>() {
                            @Override
                            public void onSuccess(Request result) {
                                if (result == null) {
                                    callback.onError(ERROR_CREATE);
                                    return;
                                }

                                callback.onSuccess(result);
                            }

                            @Override
                            public void onError(String message) {
                                callback.onError(ERROR_CREATE);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError(ERROR_CREATE);
                    }
                });
    }

    public void updateAdminAccessRequestStatus(
            String requestId,
            String status,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService().updateAdminAccessRequestStatusLegacy(status, requestId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            callback.onError(ERROR_UPDATE);
                            return;
                        }

                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError(ERROR_UPDATE);
                    }
                });
    }

    public void clearUserGroupRequestFromLocalStore(String userId, String groupId) {
        // No local fallback is used in this simplified test implementation.
    }

    private String sanitizeDescription(String description) {
        String safeDescription = description == null || description.trim().isEmpty()
                ? DEFAULT_DESCRIPTION
                : description.trim();
        safeDescription = safeDescription
                .replace("/", "_")
                .replace("\\", "_")
                .replace("?", "_")
                .replace("#", "_")
                .replace("%", "_")
                .replaceAll("\\s+", "_");
        return safeDescription.isEmpty() ? DEFAULT_DESCRIPTION : safeDescription;
    }
}
