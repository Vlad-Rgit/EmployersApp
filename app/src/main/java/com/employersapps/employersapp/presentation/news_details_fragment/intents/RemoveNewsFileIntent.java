package com.employersapps.employersapp.presentation.news_details_fragment.intents;

import com.employersapps.core.domain.NewsFile;

public class RemoveNewsFileIntent implements NewsDetailsIntent {
    private final NewsFile newsFile;

    public RemoveNewsFileIntent(NewsFile newsFile) {
        this.newsFile = newsFile;
    }

    public NewsFile getNewsFile() {
        return newsFile;
    }
}
