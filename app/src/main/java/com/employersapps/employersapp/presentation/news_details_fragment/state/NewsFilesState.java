package com.employersapps.employersapp.presentation.news_details_fragment.state;

import com.employersapps.core.domain.NewsFile;

import java.util.Collections;
import java.util.List;

public class NewsFilesState implements NewsDetailsState {
    private final List<NewsFile> newsFiles;

    public NewsFilesState(List<NewsFile> newsFiles) {
        this.newsFiles = newsFiles;
    }

    public List<NewsFile> getNewsFiles() {
        return newsFiles;
    }
}
