package com.example.manageit.apis.marketaux.models;

import com.google.gson.annotations.SerializedName;

/**
 * Single Marketaux news article summary.
 */
public class MarketauxArticle {

    private String uuid;
    private String title;
    private String description;
    private String url;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("published_at")
    private String publishedAt;
    private String source;

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title == null || title.trim().isEmpty() ? "Finance news article" : title;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getSource() {
        return source == null || source.trim().isEmpty() ? "Marketaux" : source;
    }
}
