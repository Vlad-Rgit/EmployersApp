package com.employersapps.core.domain;


import com.employersapps.core.domain.network.NewsFileNetwork;
import com.employersapps.core.domain.network.NewsImageNetwork;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class News implements ListItem<News>, Serializable {

    private final long id;
    private final long userId;
    private final String title;
    private final String text;
    private final LocalDateTime createdAt;
    private final List<NewsImageNetwork> newsPhotos;
    private final List<NewsFileNetwork> newsFiles;
    private final Employer user;

    public News(long id,
                long userId,
                String title,
                String text,
                LocalDateTime createdAt,
                List<NewsImageNetwork> newsPhotos,
                List<NewsFileNetwork> newsFiles,
                Employer user) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.text = text;
        this.createdAt = createdAt;
        this.newsPhotos = newsPhotos == null ? Collections.emptyList() : newsPhotos;
        this.newsFiles = newsFiles == null ? Collections.emptyList() : newsFiles;
        this.user = user;
    }

    public News(long userId, String title, String text, LocalDateTime createdAt, Employer user) {
        this(-1,
                userId,
                title,
                text,
                createdAt,
                Collections.emptyList(),
                Collections.emptyList(),
                user);
    }

    public News(long userId, String title, String text) {
        this(-1,
                userId,
                title,
                text,
                LocalDateTime.now(),
                Collections.emptyList(),
                Collections.emptyList(),
                null);
    }


    public @Nullable Employer getUser() {
        return user;
    }

    public long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<NewsImageNetwork> getNewsPhotos() {
        return newsPhotos;
    }

    public List<NewsFileNetwork> getNewsFiles() {
        return newsFiles;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return id == news.id &&
                userId == news.userId &&
                Objects.equals(title, news.title) &&
                Objects.equals(text, news.text) &&
                Objects.equals(createdAt, news.createdAt) &&
                Objects.equals(newsPhotos, news.newsPhotos) &&
                Objects.equals(newsFiles, news.newsFiles) &&
                Objects.equals(user, news.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, title, text, createdAt, newsPhotos, newsFiles, user);
    }

    @Override
    public boolean areItemsTheSame(News other) {
        return this.equals(other);
    }

    @Override
    public boolean areContentsTheSame(News other) {
        return this.id == other.id;
    }
}
