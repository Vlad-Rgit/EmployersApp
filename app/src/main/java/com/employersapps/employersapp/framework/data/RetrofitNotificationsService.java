package com.employersapps.employersapp.framework.data;

import com.employersapps.core.domain.AdminNotification;
import com.employersapps.core.domain.network.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitNotificationsService {
    @POST("/notifications")
    Call<ServerResponse> postNotifications(@Body AdminNotification notification);
}
