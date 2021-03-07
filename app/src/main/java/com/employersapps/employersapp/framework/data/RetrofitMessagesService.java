package com.employersapps.employersapp.framework.data;


import com.employersapps.core.domain.LastMessageChat;
import com.employersapps.core.domain.Message;
import com.employersapps.core.domain.MessageAttachment;
import com.employersapps.core.domain.network.ServerResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitMessagesService {
    @GET("/messages/{receiverId}")
    Call<List<LastMessageChat>> getMessages(@Path("receiverId") long receiverId);
    @Multipart
    @POST("/messages")
    Call<ServerResponse> postMessage(@Part("message") RequestBody message,
                                     @Part List<MultipartBody.Part> files);
}
