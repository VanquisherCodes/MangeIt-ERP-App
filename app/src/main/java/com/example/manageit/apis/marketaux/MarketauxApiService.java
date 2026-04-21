package com.example.manageit.apis.marketaux;

import com.example.manageit.apis.marketaux.models.MarketauxNewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Marketaux finance news API contract.
 */
public interface MarketauxApiService {

    @GET("v1/news/all")
    Call<MarketauxNewsResponse> getFinanceNews(
            @Query("api_token") String apiToken,
            @Query("language") String language,
            @Query("limit") int limit,
            @Query("search") String search,
            @Query("must_have_entities") boolean mustHaveEntities
    );
}
