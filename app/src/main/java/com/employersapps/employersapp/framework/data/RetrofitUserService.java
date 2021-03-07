package com.employersapps.employersapp.framework.data;

import com.employersapps.core.domain.network.LoginResponse;
import com.employersapps.core.domain.UserLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitUserService {
    @POST("/users/login")
    Call<LoginResponse> login(@Body UserLogin userLogin);
}
