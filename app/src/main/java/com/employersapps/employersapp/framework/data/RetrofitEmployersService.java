package com.employersapps.employersapp.framework.data;

import androidx.room.Delete;

import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.network.ServerResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitEmployersService {
    @GET("/users")
    Call<List<Employer>> getEmployers();
    @GET("/users/{userId}")
    Call<Employer> getEmployer(@Path("userId") long userId);
    @PUT("/users/{userId}/{statusId}")
    Call<ServerResponse> updateStatus(@Path("userId") long userId, @Path("statusId") int statusId);

    @Multipart
    @POST("/employer")
    Call<ServerResponse> addUser(@Part("employer") RequestBody body,
                                 @Part MultipartBody.Part photo);

    @Multipart
    @PUT("/employer")
    Call<ServerResponse> updateUser(@Part("employer")RequestBody body,
                                 @Part List<MultipartBody.Part> photo);

    @DELETE("/employer/{id}")
    Call<ServerResponse> deleteUser(@Path("id") long id);
}
