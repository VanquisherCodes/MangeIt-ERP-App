package com.example.manageit.apis.marketaux.models;

import java.util.Collections;
import java.util.List;

/**
 * Marketaux finance news response wrapper.
 */
public class MarketauxNewsResponse {

    private List<MarketauxArticle> data;

    public List<MarketauxArticle> getData() {
        return data == null ? Collections.emptyList() : data;
    }
}
