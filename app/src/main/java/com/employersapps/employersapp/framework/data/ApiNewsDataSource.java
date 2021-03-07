package com.employersapps.employersapp.framework.data;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.employersapps.core.data.NewsDataSource;
import com.employersapps.core.domain.NewsFile;
import com.employersapps.core.domain.NewsImage;
import com.employersapps.core.domain.network.NewsFileNetwork;
import com.employersapps.core.domain.network.NewsImageNetwork;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.domain.News;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;
import com.employersapps.employersapp.framework.util.ContentHelper;
import com.employersapps.employersapp.framework.util.StreamHelper;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiNewsDataSource implements NewsDataSource {

    private final BehaviorSubject<List<News>> newsSubject =
            BehaviorSubject.create();

    private final BehaviorSubject<Throwable> exceptionsSubject =
            BehaviorSubject.create();

    private final RetrofitNewsService newsService;

    private final Gson gson;

    private final Context context;

    @Inject
    public ApiNewsDataSource(RetrofitNewsService newsService, Gson gson, Context context) {
        this.newsService = newsService;
        this.gson = gson;
        this.context = context;
    }

    @Override
    public @NotNull Subject<List<News>> getNewsSubject() {
        return newsSubject;
    }

    @Override
    public @NotNull Subject<Throwable> getExceptionsSubject() {
        return exceptionsSubject;
    }

    @Override
    public void refreshNews() {
        newsService.getNews().enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if(response.isSuccessful()) {
                    newsSubject.onNext(response.body());
                }
                else {

                    ServerResponse serverResponse = gson.fromJson(
                            response.errorBody().charStream(),
                            ServerResponse.class
                    );

                    exceptionsSubject.onNext(new IllegalRequestException(serverResponse));
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                exceptionsSubject.onNext(t);
            }
        });
    }


    @Override
    public @NotNull Deferrable<News> addNews(News news) {
        Deferrable<News> deferrable = new Deferrable<>();

        newsService.addNews(news).enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful()) {
                    deferrable.complete(response.body());
                }
                else {

                    ServerResponse serverResponse = gson.fromJson(
                            response.errorBody().charStream(),
                            ServerResponse.class
                    );

                    deferrable.completeWithException(
                            new IllegalRequestException(serverResponse)
                    );
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                deferrable.completeWithException(t);
            }
        });

        return deferrable;
    }

    @Override
    public @NotNull Deferrable<News> addNewsWithFiles(News news, List<NewsImage> newsImages, List<NewsFile> newsFiles) {
        Deferrable<News> deferrable = new Deferrable<>();

        List<MultipartBody.Part> imagesParts = new ArrayList<>(newsImages.size());
        List<MultipartBody.Part> filesParts = new ArrayList<>(newsFiles.size());

        ContentResolver resolver = context.getContentResolver();

        try {
            for (NewsImage newsImage : newsImages) {

                if(newsImage.isNetwork()) {
                    continue;
                }

                Uri uri = Uri.parse(newsImage.getUri());

                byte[] content = StreamHelper.readAllBytes(
                        resolver.openInputStream(uri));

                RequestBody body = RequestBody.create(
                        MediaType.parse(resolver.getType(uri)),
                        content
                );

                MultipartBody.Part part = MultipartBody.Part
                        .createFormData("images",
                                ContentHelper.getFileName(context, uri),
                                body);

                imagesParts.add(part);
            }

            for(NewsFile newsFile : newsFiles) {

                if(newsFile.isNetwork()) {
                    continue;
                }

                Uri uri = Uri.parse(newsFile.getUriString());

                byte[] content = StreamHelper.readAllBytes(
                        resolver.openInputStream(uri)
                );

                RequestBody body = RequestBody.create(
                        MediaType.parse(resolver.getType(uri)),
                        content
                );

                MultipartBody.Part part = MultipartBody.Part
                        .createFormData("files",
                                ContentHelper.getFileName(context, uri),
                                body);

                filesParts.add(part);
            }

            RequestBody newsBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    gson.toJson(news)
            );

            newsService.addNewsWithFiles(newsBody, imagesParts, filesParts).enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    if(response.isSuccessful()) {
                        deferrable.complete(response.body());
                    }
                    else {
                        deferrable.completeWithException(
                                new IllegalRequestException(response.code(),
                                        response.message()));
                    }
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    deferrable.completeWithException(t);
                }
            });

        }
        catch (Exception ex) {
            ex.printStackTrace();
            deferrable.completeWithException(ex);
        }


        return deferrable;

    }

    @Override
    public @NotNull Deferrable<News> updateNewsWithFiles(News news, List<NewsImage> newsImages, List<NewsFile> newsFiles) {
        Deferrable<News> deferrable = new Deferrable<>();

        List<MultipartBody.Part> imagesParts = new ArrayList<>(newsImages.size());
        List<MultipartBody.Part> filesParts = new ArrayList<>(newsFiles.size());

        ContentResolver resolver = context.getContentResolver();

        try {
            for (NewsImage newsImage : newsImages) {

                if(newsImage.isNetwork()) {
                    continue;
                }

                Uri uri = Uri.parse(newsImage.getUri());

                byte[] content = StreamHelper.readAllBytes(
                        resolver.openInputStream(uri));

                RequestBody body = RequestBody.create(
                        MediaType.parse(resolver.getType(uri)),
                        content
                );

                MultipartBody.Part part = MultipartBody.Part
                        .createFormData("images",
                                ContentHelper.getFileName(context, uri),
                                body);

                imagesParts.add(part);
            }

            for(NewsFile newsFile : newsFiles) {

                if(newsFile.isNetwork()) {
                    continue;
                }

                Uri uri = Uri.parse(newsFile.getUriString());

                byte[] content = StreamHelper.readAllBytes(
                        resolver.openInputStream(uri)
                );

                RequestBody body = RequestBody.create(
                        MediaType.parse(resolver.getType(uri)),
                        content
                );

                MultipartBody.Part part = MultipartBody.Part
                        .createFormData("files",
                                ContentHelper.getFileName(context, uri),
                                body);

                filesParts.add(part);
            }

            RequestBody newsBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    gson.toJson(news)
            );

            newsService.updateNewsWithFiles(newsBody, imagesParts, filesParts).enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    if(response.isSuccessful()) {
                        deferrable.complete(response.body());
                    }
                    else {
                        deferrable.completeWithException(
                                new IllegalRequestException(response.code(),
                                        response.message()));
                    }
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    deferrable.completeWithException(t);
                }
            });

        }
        catch (Exception ex) {
            ex.printStackTrace();
            deferrable.completeWithException(ex);
        }


        return deferrable;
    }

    @Override
    public @NotNull Deferrable<ServerResponse> deleteNews(News news) {
        Deferrable<ServerResponse> deferrable = new Deferrable<>();

        newsService.deleteMessage(news.getId()).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()) {
                    deferrable.complete(response.body());
                }
                else {
                    deferrable.completeWithException(new IllegalRequestException(
                            response.code(),
                            response.message()
                    ));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                deferrable.completeWithException(t);
            }
        });

        return deferrable;
    }

    @Override
    public @NotNull Deferrable<ServerResponse> deleteNewsPhoto(NewsImageNetwork newsImage) {
        Deferrable<ServerResponse> deferrable = new Deferrable<>();

        newsService.deletePhoto(newsImage.getName()).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()) {
                    deferrable.complete(response.body());
                }
                else {
                    deferrable.completeWithException(new IllegalRequestException(
                            response.code(),
                            response.message()
                    ));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                deferrable.completeWithException(t);
            }
        });

        return deferrable;
    }

    @Override
    public @NotNull Deferrable<ServerResponse> deleteNewsFile(NewsFileNetwork newsFileNetwork) {
        Deferrable<ServerResponse> deferrable = new Deferrable<>();

        newsService.deleteFile(newsFileNetwork.getName()).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()) {
                    deferrable.complete(response.body());
                }
                else {
                    deferrable.completeWithException(new IllegalRequestException(
                            response.code(),
                            response.message()
                    ));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                deferrable.completeWithException(t);
            }
        });

        return deferrable;
    }


}
