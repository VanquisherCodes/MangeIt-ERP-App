package com.example.manageit.repository;

/**
 * Simple callback for repository operations that complete asynchronously.
 */
public interface RepositoryCallback<T> {
    void onSuccess(T result);

    void onError(String message);
}
