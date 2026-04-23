package com.example.manageit.repository;

import com.example.manageit.models.Event;
import com.example.manageit.network.ApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Event operations scoped to one student group.
 */
public class GroupEventsRepository {

    private final ApiClient apiClient;

    public GroupEventsRepository() {
        this.apiClient = ApiClient.getInstance();
    }

    public void getGroupEvents(String groupId, RepositoryCallback<List<Event>> callback) {
        apiClient.getApiService().getGroupEventsRest(groupId).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                getGroupEventsLegacy(groupId, callback);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable throwable) {
                getGroupEventsLegacy(groupId, callback);
            }
        });
    }

    public void createEvent(
            String groupId,
            String title,
            String description,
            String eventDateTime,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("description", description == null ? "" : description);
        body.put("datetime", eventDateTime);
        body.put("createdByMembershipId", createdByMembershipId == null ? "" : createdByMembershipId);

        apiClient.getApiService().createEventRest(groupId, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                    return;
                }
                createEventLegacy(groupId, title, description, eventDateTime, createdByMembershipId, callback);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                createEventLegacy(groupId, title, description, eventDateTime, createdByMembershipId, callback);
            }
        });
    }

    public void updateEvent(
            String eventId,
            String title,
            String description,
            String eventDateTime,
            RepositoryCallback<Void> callback
    ) {
        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("description", description == null ? "" : description);
        body.put("datetime", eventDateTime);

        apiClient.getApiService().updateEventRest(eventId, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                    return;
                }
                updateEventLegacy(eventId, title, description, eventDateTime, callback);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                updateEventLegacy(eventId, title, description, eventDateTime, callback);
            }
        });
    }

    public void deleteEvent(String eventId, RepositoryCallback<Void> callback) {
        apiClient.getApiService().deleteEventRest(eventId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                    return;
                }
                deleteEventLegacy(eventId, callback);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                deleteEventLegacy(eventId, callback);
            }
        });
    }

    private void getGroupEventsLegacy(String groupId, RepositoryCallback<List<Event>> callback) {
        apiClient.getApiService().getGroupEventsLegacy(groupId).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Couldn't load group events right now.");
                    return;
                }
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable throwable) {
                callback.onError("Couldn't reach the server while loading events.");
            }
        });
    }

    private void createEventLegacy(
            String groupId,
            String title,
            String description,
            String eventDateTime,
            String createdByMembershipId,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .createEventLegacy(
                        groupId,
                        title,
                        description == null ? "-" : description,
                        eventDateTime,
                        createdByMembershipId == null ? "" : createdByMembershipId
                )
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            callback.onError("Couldn't create the group event.");
                            return;
                        }
                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while creating the event.");
                    }
                });
    }

    private void updateEventLegacy(
            String eventId,
            String title,
            String description,
            String eventDateTime,
            RepositoryCallback<Void> callback
    ) {
        apiClient.getApiService()
                .updateEventLegacy(eventId, title, description == null ? "-" : description, eventDateTime)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            callback.onError("Couldn't update this event.");
                            return;
                        }
                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        callback.onError("Couldn't reach the server while updating this event.");
                    }
                });
    }

    private void deleteEventLegacy(String eventId, RepositoryCallback<Void> callback) {
        apiClient.getApiService().deleteEventLegacy(eventId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    callback.onError("Couldn't delete this event.");
                    return;
                }
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onError("Couldn't reach the server while deleting this event.");
            }
        });
    }
}
