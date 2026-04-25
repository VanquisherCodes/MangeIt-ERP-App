package com.example.manageit.domain.requests;

import com.example.manageit.models.Request;
import com.example.manageit.repository.RepositoryCallback;
import com.example.manageit.repository.contracts.AdminAccessRequestRepositoryContract;

public class RequestAdminAccessUseCase {

    private final AdminAccessRequestRepositoryContract repository;

    public RequestAdminAccessUseCase(AdminAccessRequestRepositoryContract repository) {
        this.repository = repository;
    }

    public void execute(
            String userId,
            String groupId,
            String description,
            String requesterFirstName,
            String requesterLastName,
            String requesterEmail,
            RepositoryCallback<Request> callback
    ) {
        repository.createAdminAccessRequest(
                userId,
                groupId,
                description,
                requesterFirstName,
                requesterLastName,
                requesterEmail,
                callback
        );
    }
}
