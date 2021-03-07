package com.employersapps.employersapp.presentation.news_fragment.intents;

import com.employersapps.core.domain.News;

public class AddNewsIntent implements NewsFragmentIntent {

    private final News news;

    public AddNewsIntent(News news) {
        this.news = news;
    }

    public News getNews() {
        return news;
    }
}
