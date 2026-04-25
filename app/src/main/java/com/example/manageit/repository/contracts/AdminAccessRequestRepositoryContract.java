package com.example.manageit.repository.contracts;

import com.example.manageit.models.Request;
import com.example.manageit.repository.RepositoryCallback;

public interface AdminAccessRequestRepositoryContract {
    void createAdminAccessRequest(
            String userId,
            String groupId,
            String description,
            String requesterFirstName,
            String requesterLastName,
            String requesterEmail,
            RepositoryCallback<Request> callback
    );
}
