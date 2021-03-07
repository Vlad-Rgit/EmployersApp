package com.employersapps.employersapp.framework.data;

import com.employersapps.core.domain.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitPostsService {
    @GET("/posts")
    Call<List<Post>> getPosts();
}
