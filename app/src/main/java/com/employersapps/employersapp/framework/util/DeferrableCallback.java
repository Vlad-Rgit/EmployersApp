package com.employersapps.employersapp.framework.util;

import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeferrableCallback<T> implements Callback<T> {

    private final Deferrable<T> deferrable;

    public DeferrableCallback() {
        this.deferrable = new Deferrable<>();
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
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
    public void onFailure(Call<T> call, Throwable t) {
        deferrable.completeWithException(t);
    }

    public Deferrable<T> getDeferrable() {
        return deferrable;
    }
}
