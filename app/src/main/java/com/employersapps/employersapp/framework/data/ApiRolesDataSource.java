package com.employersapps.employersapp.framework.data;

import com.employersapps.core.data.RolesDataSource;
import com.employersapps.core.domain.Role;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRolesDataSource implements RolesDataSource {

    private final RetrofitRolesService retrofitRolesService;

    @Inject
    public ApiRolesDataSource(RetrofitRolesService retrofitRolesService) {
        this.retrofitRolesService = retrofitRolesService;
    }

    @Override
    public Deferrable<List<Role>> getRoles() {
        Deferrable<List<Role>> deferrable = new Deferrable<>();

        retrofitRolesService.getRoles().enqueue(new Callback<List<Role>>() {
            @Override
            public void onResponse(Call<List<Role>> call, Response<List<Role>> response) {
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
            public void onFailure(Call<List<Role>> call, Throwable t) {
                deferrable.completeWithException(t);
            }
        });

        return deferrable;
    }
}
