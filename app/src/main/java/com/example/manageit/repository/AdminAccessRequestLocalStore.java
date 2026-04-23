package com.example.manageit.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.manageit.models.Request;
import com.example.manageit.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Local fallback persistence for admin access requests when backend endpoints are unavailable.
 */
public class AdminAccessRequestLocalStore {

    private static final String REQUEST_TYPE_ADMIN_ACCESS = "admin_access";
    private static final String KEY_LOCAL_REQUESTS = "key_local_admin_access_requests";

    private final SharedPreferences preferences;
    private final Gson gson = new Gson();
    private final Type requestListType = new TypeToken<List<Request>>() {}.getType();

    public AdminAccessRequestLocalStore(Context context) {
        this.preferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized List<Request> getGroupAdminAccessRequests(String groupId) {
        List<Request> requests = readAll();
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Request> filtered = new ArrayList<>();
        for (Request request : requests) {
            if (request == null) {
                continue;
            }
            if (!REQUEST_TYPE_ADMIN_ACCESS.equalsIgnoreCase(safe(request.getRequestType()))) {
                continue;
            }
            if (!safe(groupId).equals(safe(request.getGroupId()))) {
                continue;
            }
            filtered.add(request);
        }
        return filtered;
    }

    public synchronized Request getLatestUserAdminAccessRequest(String userId, String groupId) {
        List<Request> requests = readAll();
        for (Request request : requests) {
            if (request == null) {
                continue;
            }
            if (!REQUEST_TYPE_ADMIN_ACCESS.equalsIgnoreCase(safe(request.getRequestType()))) {
                continue;
            }
            if (!safe(userId).equals(safe(request.getUserId()))) {
                continue;
            }
            if (!safe(groupId).equals(safe(request.getGroupId()))) {
                continue;
            }
            return request;
        }
        return null;
    }

    public synchronized Request createAdminAccessRequest(
            String userId,
            String groupId,
            String description,
            String requesterFirstName,
            String requesterLastName,
            String requesterEmail
    ) {
        List<Request> requests = readAll();

        Request latest = getLatestForUserAndGroup(requests, userId, groupId);
        if (latest != null && latest.isPending()) {
            return latest;
        }

        Request created = new Request();
        created.setRequestId("local_" + System.currentTimeMillis() + "_" + Math.abs((safe(userId) + safe(groupId)).hashCode()));
        created.setRequestType(REQUEST_TYPE_ADMIN_ACCESS);
        created.setDescription(description == null || description.trim().isEmpty() ? "Requested admin access." : description.trim());
        created.setStatus("pending");
        created.setUserId(userId);
        created.setGroupId(groupId);
        created.setRequesterFirstName(requesterFirstName);
        created.setRequesterLastName(requesterLastName);
        created.setRequesterEmail(requesterEmail);
        created.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        requests.add(0, created);
        writeAll(requests);
        return created;
    }

    public synchronized boolean updateRequestStatus(String requestId, String status) {
        List<Request> requests = readAll();
        boolean updated = false;

        for (Request request : requests) {
            if (request == null) {
                continue;
            }
            if (safe(requestId).equals(safe(request.getRequestId()))) {
                request.setStatus(status);
                updated = true;
                break;
            }
        }

        if (updated) {
            writeAll(requests);
        }
        return updated;
    }

    public synchronized void clearUserGroupRequests(String userId, String groupId) {
        List<Request> requests = readAll();
        if (requests.isEmpty()) {
            return;
        }

        Iterator<Request> iterator = requests.iterator();
        boolean changed = false;
        while (iterator.hasNext()) {
            Request request = iterator.next();
            if (request == null) {
                continue;
            }
            if (!REQUEST_TYPE_ADMIN_ACCESS.equalsIgnoreCase(safe(request.getRequestType()))) {
                continue;
            }
            if (!safe(userId).equals(safe(request.getUserId()))) {
                continue;
            }
            if (!safe(groupId).equals(safe(request.getGroupId()))) {
                continue;
            }
            iterator.remove();
            changed = true;
        }

        if (changed) {
            writeAll(requests);
        }
    }

    public synchronized void clearAllRequests() {
        preferences.edit().remove(KEY_LOCAL_REQUESTS).apply();
    }

    private Request getLatestForUserAndGroup(List<Request> requests, String userId, String groupId) {
        for (Request request : requests) {
            if (request == null) {
                continue;
            }
            if (!REQUEST_TYPE_ADMIN_ACCESS.equalsIgnoreCase(safe(request.getRequestType()))) {
                continue;
            }
            if (!safe(userId).equals(safe(request.getUserId()))) {
                continue;
            }
            if (!safe(groupId).equals(safe(request.getGroupId()))) {
                continue;
            }
            return request;
        }
        return null;
    }

    private List<Request> readAll() {
        String json = preferences.getString(KEY_LOCAL_REQUESTS, "");
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Request> parsed = gson.fromJson(json, requestListType);
        return parsed == null ? new ArrayList<>() : new ArrayList<>(parsed);
    }

    private void writeAll(List<Request> requests) {
        preferences.edit().putString(KEY_LOCAL_REQUESTS, gson.toJson(requests)).apply();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
