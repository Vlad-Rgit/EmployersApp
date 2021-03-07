package com.employersapps.employersapp.framework.data;

import android.nfc.tech.NdefFormatable;

import com.employersapps.core.data.FmsTokenDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ApiFmsTokenDataSource implements FmsTokenDataSource {

    private final RetrofitFmsTokenService fmsTokenService;
    private final UserDataSource userDataSource;

    @Inject
    public ApiFmsTokenDataSource(RetrofitFmsTokenService fmsTokenService, UserDataSource userDataSource) {
        this.fmsTokenService = fmsTokenService;
        this.userDataSource = userDataSource;
    }


    @Override
    public Deferrable<ServerResponse> updateToken(String token) {
        Deferrable<ServerResponse> deferrable = new Deferrable<>();

        if(userDataSource.isLogged()) {
            long userId = userDataSource.getCurrentUser().getId();

            fmsTokenService.updateToken(userId, token).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if (response.isSuccessful()) {
                        deferrable.complete(response.body());
                    } else {
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
        }
        else {
            deferrable.completeWithException(new IllegalStateException("" +
                    "User is not authenticated"));
        }

        return deferrable;
    }
}

