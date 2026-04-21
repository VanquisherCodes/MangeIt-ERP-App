package com.example.manageit.apis.marketaux;

import com.example.manageit.BuildConfig;
import com.example.manageit.apis.marketaux.models.MarketauxArticle;
import com.example.manageit.apis.marketaux.models.MarketauxNewsResponse;
import com.example.manageit.repository.RepositoryCallback;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fetches user-facing market news from Marketaux.
 */
public class MarketauxNewsRepository {

    private final MarketauxApiService apiService;

    public MarketauxNewsRepository() {
        this.apiService = MarketauxApiClient.getService();
    }

    public void getNewsForGroup(String groupName, RepositoryCallback<List<MarketauxArticle>> callback) {
        if (BuildConfig.MARKETAUX_API_TOKEN == null || BuildConfig.MARKETAUX_API_TOKEN.trim().isEmpty()) {
            callback.onError("Marketaux API token is missing. Add marketauxApiToken to local.properties.");
            return;
        }

        apiService.getFinanceNews(
                BuildConfig.MARKETAUX_API_TOKEN,
                "en",
                3,
                buildSearch(groupName),
                false
        ).enqueue(new Callback<MarketauxNewsResponse>() {
            @Override
            public void onResponse(Call<MarketauxNewsResponse> call, Response<MarketauxNewsResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Couldn't load finance news right now.");
                    return;
                }

                callback.onSuccess(response.body().getData());
            }

            @Override
            public void onFailure(Call<MarketauxNewsResponse> call, Throwable throwable) {
                callback.onError("Couldn't reach the Marketaux news API.");
            }
        });
    }

    private String buildSearch(String groupName) {
        String normalizedGroup = groupName == null ? "" : groupName.toLowerCase(Locale.US);
        if (normalizedGroup.contains("robot")) {
            return "robotics automation engineering manufacturing technology";
        }
        if (normalizedGroup.contains("ai")) {
            return "artificial intelligence machine learning semiconductor technology";
        }
        if (normalizedGroup.contains("debate")) {
            return "economics policy politics markets global business";
        }
        if (normalizedGroup.contains("design")) {
            return "design media branding technology creative industry";
        }
        return "business markets finance technology";
    }
}
