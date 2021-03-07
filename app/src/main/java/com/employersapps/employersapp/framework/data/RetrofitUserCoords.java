package com.employersapps.employersapp.framework.data;

import com.employersapps.core.domain.UserCoords;
import com.employersapps.core.domain.network.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitUserCoords {
    @POST("/usercoords")
    Call<ServerResponse> addUserCoords(@Body UserCoords userCoords);
}
