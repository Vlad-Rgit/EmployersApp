package com.employersapps.employersapp.framework.data;

import androidx.room.Delete;

import com.employersapps.core.domain.News;
import com.employersapps.core.domain.network.NewsImageNetwork;
import com.employersapps.core.domain.network.ServerResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitNewsService {
    @GET("/news")
    Call<List<News>> getNews();

    @POST("/news")
    Call<News> addNews(@Body News news);

    @Multipart
    @POST("/news")
    Call<News> addNewsWithFiles(
            @Part("news") RequestBody newsBody,
            @Part List<MultipartBody.Part> images,
            @Part List<MultipartBody.Part> files
    );

    @Multipart
    @PUT("/news")
    Call<News> updateNewsWithFiles(
            @Part("news") RequestBody newsBody,
            @Part List<MultipartBody.Part> images,
            @Part List<MultipartBody.Part> files
    );

    @GET("/news/images/{newsId}")
    Call<List<NewsImageNetwork>> getNewsImages(@Path("newsId") long newsId);

    @DELETE("/news/{newsId}")
    Call<ServerResponse> deleteMessage(@Path("newsId") long newsId);

    @DELETE("/news/file/{id}")
    Call<ServerResponse> deleteFile(@Path("id") String fileName);

    @DELETE("/news/photo/{id}")
    Call<ServerResponse> deletePhoto(@Path("id") String photoName);

 }
