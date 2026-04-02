package com.example.manageit.network;

/**
 * Single place for API base URL and future Retrofit/OkHttp initialization.
 */
public class ApiClient {

    private static ApiClient instance;
    private String baseUrl = "https://api.example.com";

    private ApiClient() {
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
