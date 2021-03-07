package com.employersapps.employersapp.presentation.news_fragment.states;

import com.employersapps.core.domain.News;

import java.util.Collections;
import java.util.List;

public class ListNewsState implements NewsFragmentState {

    private final List<News> news;

    public ListNewsState(List<News> news) {
        this.news = Collections.unmodifiableList(news);
    }

    public List<News> getNews() {
        return news;
    }
}
