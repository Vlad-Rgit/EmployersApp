package com.employersapps.employersapp.presentation.news_fragment.intents;

import com.employersapps.core.domain.News;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.state.ErrorState;
import com.employersapps.core.state.SuccessState;

public class DeleteNewsIntent implements NewsFragmentIntent {

    private final News news;

    public DeleteNewsIntent(News news) {
        this.news = news;
    }

    public News getNews() {
        return news;
    }
}
