package com.employersapps.core.data;


import com.employersapps.core.domain.News;
import com.employersapps.core.domain.NewsFile;
import com.employersapps.core.domain.NewsImage;
import com.employersapps.core.domain.network.NewsFileNetwork;
import com.employersapps.core.domain.network.NewsImageNetwork;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.subjects.Subject;

public interface NewsDataSource {
    @NotNull Subject<List<News>> getNewsSubject();
    @NotNull Subject<Throwable> getExceptionsSubject();
    void refreshNews();
    @NotNull Deferrable<News> addNews(News news);
    @NotNull Deferrable<News> addNewsWithFiles(News news, List<NewsImage> newsImages, List<NewsFile> newsFiles);
    @NotNull Deferrable<News> updateNewsWithFiles(News news, List<NewsImage> newsImages, List<NewsFile> newsFiles);
    @NotNull Deferrable<ServerResponse> deleteNews(News news);
    @NotNull Deferrable<ServerResponse> deleteNewsPhoto(NewsImageNetwork newsImage);
    @NotNull Deferrable<ServerResponse> deleteNewsFile(NewsFileNetwork newsFileNetwork);
}
