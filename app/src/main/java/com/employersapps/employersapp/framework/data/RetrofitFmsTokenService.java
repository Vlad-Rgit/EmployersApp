package com.employersapps.employersapp.framework.data;

import com.employersapps.core.domain.network.ServerResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitFmsTokenService {
    @POST("/users/fms-token/{userId}")
    Call<ServerResponse> updateToken(@Path("userId") long userId,
                                     @Query("token") String token);
}
