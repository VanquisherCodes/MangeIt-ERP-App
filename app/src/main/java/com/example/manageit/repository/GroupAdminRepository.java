package com.example.manageit.repository;

import com.example.manageit.models.GroupMember;
import com.example.manageit.models.Role;
import com.example.manageit.network.ApiClient;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Admin-only membership management operations.
 */
public class GroupAdminRepository {

    private final ApiClient apiClient;

    public GroupAdminRepository() {
        this.apiClient = ApiClient.getInstance();
    }

    public void getGroupMembers(String groupId, RepositoryCallback<List<GroupMember>> callback) {
        apiClient.getApiService().getGroupMembers(groupId).enqueue(new Callback<List<GroupMember>>() {
            @Override
            public void onResponse(Call<List<GroupMember>> call, Response<List<GroupMember>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Couldn't load group members right now.");
                    return;
                }

                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<GroupMember>> call, Throwable throwable) {
                callback.onError("Couldn't reach the server while loading members.");
            }
        });
    }

    public void updateMemberRole(GroupMember member, Role newRole, RepositoryCallback<GroupMember> callback) {
        apiClient.getApiService()
                .updateMemberRole(member.getMembershipId(), newRole.name().toLowerCase())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            callback.onError("Couldn't update this member's role.");
                            return;
                        }

                        member.setRoleInGroup(newRole);
                        callback.onSuccess(member);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while updating the role.");
                    }
                });
    }
}
