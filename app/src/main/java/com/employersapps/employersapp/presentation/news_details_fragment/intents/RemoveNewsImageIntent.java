package com.employersapps.employersapp.presentation.news_details_fragment.intents;

import com.employersapps.core.domain.NewsImage;

public class RemoveNewsImageIntent implements NewsDetailsIntent{
    private final NewsImage newsImage;

    public RemoveNewsImageIntent(NewsImage newsImage) {
        this.newsImage = newsImage;
    }

    public NewsImage getNewsImage() {
        return newsImage;
    }
}
