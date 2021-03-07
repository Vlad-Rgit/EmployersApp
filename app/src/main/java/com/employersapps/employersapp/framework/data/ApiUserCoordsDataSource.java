package com.employersapps.employersapp.framework.data;

import android.graphics.DashPathEffect;
import android.nfc.tech.NdefFormatable;

import com.employersapps.core.data.UserCoordsDataSource;
import com.employersapps.core.domain.UserCoords;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiUserCoordsDataSource implements UserCoordsDataSource {


    private final RetrofitUserCoords retrofitUserCoords;

    @Inject
    public ApiUserCoordsDataSource(RetrofitUserCoords retrofitUserCoords) {
        this.retrofitUserCoords = retrofitUserCoords;
    }

    @Override
    public Deferrable<Void> sendLocation(UserCoords userCoords) {

        Deferrable<Void> deferrable = new Deferrable<>();

        retrofitUserCoords.addUserCoords(userCoords).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()) {
                    deferrable.complete(null);
                }
                else {
                    deferrable.completeWithException(
                            new IllegalRequestException(response.code(), response.message())
                    );
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
