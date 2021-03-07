package com.employersapps.employersapp.presentation.news_details_fragment.intents;

import com.employersapps.core.domain.NewsImage;

import java.util.Collections;
import java.util.List;

public class AddMultipleImageIntent implements NewsDetailsIntent {

    private final List<NewsImage> newsImages;

    public AddMultipleImageIntent(List<NewsImage> newsImages) {
        this.newsImages = Collections.unmodifiableList(newsImages);
    }

    public List<NewsImage> getNewsImages() {
        return newsImages;
    }
}
