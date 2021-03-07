package com.employersapps.employersapp.presentation.news_details_fragment.state;

import com.employersapps.core.domain.NewsImage;

import java.util.Collections;
import java.util.List;

public class NewsImagesListState implements NewsDetailsState {

    private final List<NewsImage> newsImages;

    public NewsImagesListState(List<NewsImage> newsImages) {
        this.newsImages = Collections.unmodifiableList(newsImages);
    }

    public List<NewsImage> getNewsImages() {
        return newsImages;
    }
}
