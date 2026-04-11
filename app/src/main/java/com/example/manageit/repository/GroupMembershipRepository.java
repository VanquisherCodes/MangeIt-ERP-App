package com.example.manageit.repository;

import com.example.manageit.models.GroupMembership;
import com.example.manageit.models.Role;
import com.example.manageit.network.ApiClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Group membership data operations backed by StudEV services.
 */
public class GroupMembershipRepository {

    private final ApiClient apiClient;

    public GroupMembershipRepository() {
        this.apiClient = ApiClient.getInstance();
    }

    public void getMembership(String userId, String groupId, RepositoryCallback<GroupMembership> callback) {
        apiClient.getApiService().getUserGroupMembership(userId, groupId)
                .enqueue(new Callback<List<GroupMembership>>() {
                    @Override
                    public void onResponse(Call<List<GroupMembership>> call, Response<List<GroupMembership>> response) {
                        if (!response.isSuccessful()) {
                            callback.onError("Couldn't check your membership for this group.");
                            return;
                        }

                        List<GroupMembership> memberships = response.body();
                        if (memberships == null || memberships.isEmpty()) {
                            callback.onSuccess(null);
                            return;
                        }

                        callback.onSuccess(memberships.get(0));
                    }

                    @Override
                    public void onFailure(Call<List<GroupMembership>> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while checking membership.");
                    }
                });
    }

    public void createMembership(
            String userId,
            String groupId,
            Role roleInGroup,
            RepositoryCallback<GroupMembership> callback
    ) {
        apiClient.getApiService()
                .createGroupMembership(userId, groupId, roleInGroup.name().toLowerCase())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            callback.onError("Couldn't create the membership. Please try again.");
                            return;
                        }

                        getMembership(userId, groupId, new RepositoryCallback<GroupMembership>() {
                            @Override
                            public void onSuccess(GroupMembership result) {
                                if (result != null) {
                                    callback.onSuccess(result);
                                    return;
                                }

                                callback.onSuccess(buildPendingMembership(userId, groupId, roleInGroup));
                            }

                            @Override
                            public void onError(String message) {
                                callback.onSuccess(buildPendingMembership(userId, groupId, roleInGroup));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while creating the membership.");
                    }
                });
    }

    private GroupMembership buildPendingMembership(String userId, String groupId, Role roleInGroup) {
        return new GroupMembership(
                "",
                userId,
                groupId,
                roleInGroup,
                "",
                "active"
        );
    }
}
