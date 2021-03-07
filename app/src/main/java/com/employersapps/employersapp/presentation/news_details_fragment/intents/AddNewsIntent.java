package com.employersapps.employersapp.presentation.news_details_fragment.intents;

import com.employersapps.core.domain.News;

public class AddNewsIntent implements NewsDetailsIntent {

    private final News news;

    public AddNewsIntent(News news) {
        this.news = news;
    }

    public News getNews() {
        return news;
    }
}
