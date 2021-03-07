package com.employersapps.employersapp.presentation.news_details_fragment.state;

import com.employersapps.core.domain.News;

public class NewsAddedState implements NewsDetailsState {
    private final News addedNews;

    public NewsAddedState(News addedNews) {
        this.addedNews = addedNews;
    }

    public News getAddedNews() {
        return addedNews;
    }
}
