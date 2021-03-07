package com.employersapps.core.domain;

import java.util.Objects;

public class NewsImage implements ListItem<NewsImage> {

    private final String uri;
    private final boolean isNetwork;

    public NewsImage(String uri, boolean isNetwork) {
        this.uri = uri;
        this.isNetwork = isNetwork;
    }

    public NewsImage(String uri) {
        this(uri, false);
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsImage newsImage = (NewsImage) o;
        return Objects.equals(uri, newsImage.uri);
    }

    public boolean isNetwork() {
        return isNetwork;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public boolean areItemsTheSame(NewsImage other) {
        return this.equals(other);
    }

    @Override
    public boolean areContentsTheSame(NewsImage other) {
        return this.equals(other);
    }
}
