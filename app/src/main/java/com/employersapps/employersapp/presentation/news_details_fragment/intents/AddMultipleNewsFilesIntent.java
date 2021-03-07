package com.employersapps.employersapp.presentation.news_details_fragment.intents;

import com.employersapps.core.domain.NewsFile;

import java.util.List;

public class AddMultipleNewsFilesIntent implements NewsDetailsIntent {
    private final List<NewsFile> newsFiles;

    public AddMultipleNewsFilesIntent(List<NewsFile> newsFiles) {
        this.newsFiles = newsFiles;
    }

    public List<NewsFile> getNewsFiles() {
        return newsFiles;
    }
}
