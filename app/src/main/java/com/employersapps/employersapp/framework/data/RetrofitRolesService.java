package com.employersapps.employersapp.framework.data;

import com.employersapps.core.domain.Role;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitRolesService {
    @GET("/roles")
    Call<List<Role>> getRoles();
}
