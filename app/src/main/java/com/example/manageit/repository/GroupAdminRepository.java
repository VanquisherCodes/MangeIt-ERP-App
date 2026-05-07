package com.example.manageit.repository;

import com.example.manageit.models.GroupMember;
import com.example.manageit.models.Role;
import com.example.manageit.network.ApiClient;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Admin-only membership management operations.
 */
public class GroupAdminRepository {

    private static final String ERROR_UPDATE_ROLE = "Couldn't update this member's role.";
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
        if (member == null
                || isBlank(member.getMembershipId())
                || isBlank(member.getGroupId())
                || newRole == null) {
            callback.onError(ERROR_UPDATE_ROLE);
            return;
        }

        apiClient.getApiService()
                .updateMemberRole(member.getMembershipId(), newRole.name().toLowerCase())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String body = readBody(response.body());
                        if (!response.isSuccessful() || looksLikeServerError(body)) {
                            callback.onError(ERROR_UPDATE_ROLE);
                            return;
                        }

                        verifyMemberRole(member, newRole, callback);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while updating the role.");
                    }
                });
    }

    private void verifyMemberRole(
            GroupMember originalMember,
            Role expectedRole,
            RepositoryCallback<GroupMember> callback
    ) {
        apiClient.getApiService().getGroupMembers(originalMember.getGroupId()).enqueue(new Callback<List<GroupMember>>() {
            @Override
            public void onResponse(Call<List<GroupMember>> call, Response<List<GroupMember>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Role update was sent, but the updated member could not be verified.");
                    return;
                }

                GroupMember refreshedMember = findMember(response.body(), originalMember.getMembershipId());
                if (refreshedMember == null || refreshedMember.getRoleInGroup() != expectedRole) {
                    callback.onError("Role update did not persist. Please try again.");
                    return;
                }

                callback.onSuccess(refreshedMember);
            }

            @Override
            public void onFailure(Call<List<GroupMember>> call, Throwable throwable) {
                callback.onError("Role update was sent, but the updated member could not be verified.");
            }
        });
    }

    private GroupMember findMember(List<GroupMember> members, String membershipId) {
        if (members == null || membershipId == null) {
            return null;
        }

        for (GroupMember member : members) {
            if (member == null || member.getMembershipId() == null) {
                continue;
            }
            if (membershipId.trim().equals(member.getMembershipId().trim())) {
                return member;
            }
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
}
