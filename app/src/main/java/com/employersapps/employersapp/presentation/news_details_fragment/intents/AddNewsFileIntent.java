package com.employersapps.employersapp.presentation.news_details_fragment.intents;

import com.employersapps.core.domain.NewsFile;

public class AddNewsFileIntent implements NewsDetailsIntent {

    private final NewsFile newsFile;

    public AddNewsFileIntent(NewsFile newsFile) {
        this.newsFile = newsFile;
    }

    public NewsFile getNewsFile() {
        return newsFile;
    }
}
