package com.employersapps.employersapp.framework.data;

import android.nfc.tech.NdefFormatable;

import com.employersapps.core.data.AdminNotificationsDataSource;
import com.employersapps.core.domain.AdminNotification;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiAdminNotificationDataSource implements AdminNotificationsDataSource {

    private RetrofitNotificationsService notificationsService;

    @Inject
    public ApiAdminNotificationDataSource(RetrofitNotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @Override
    public Deferrable<ServerResponse> postNotification(AdminNotification notification) {
        Deferrable<ServerResponse> deferrable = new Deferrable<>();

        notificationsService.postNotifications(notification).enqueue(new Callback<ServerResponse>() {
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
