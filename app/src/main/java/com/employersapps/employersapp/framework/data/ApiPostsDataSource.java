package com.employersapps.employersapp.framework.data;

import com.employersapps.core.data.PostDataSource;
import com.employersapps.core.domain.Post;
import com.employersapps.core.domain.Role;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiPostsDataSource implements PostDataSource {

    private final RetrofitPostsService retrofitRolesService;

    @Inject
    public ApiPostsDataSource(RetrofitPostsService retrofitRolesService) {
        this.retrofitRolesService = retrofitRolesService;
    }

    @Override
    public Deferrable<List<Post>> getPosts() {

        Deferrable<List<Post>> deferrable = new Deferrable<>();

        retrofitRolesService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
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
            public void onFailure(Call<List<Post>> call, Throwable t) {
                deferrable.completeWithException(t);
            }
        });

        return deferrable;
    }
}
