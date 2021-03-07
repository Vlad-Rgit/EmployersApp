package com.employersapps.employersapp.framework.data;

import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.PostGroupChat;
import com.employersapps.core.domain.PostPrivateChat;
import com.employersapps.core.domain.network.ServerResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitChatService {
    @GET("/chats/{chatId}")
    Call<Chat> getChatDetails(@Path("chatId") long chatId);
    @POST("/chats/privateChat")
    Call<Chat> postPrivateChat(@Body PostPrivateChat postPrivateChat);
    @Multipart
    @POST("/chats/groupChat")
    Call<Chat> postGroupChat(@Part("chat") RequestBody postGroupChat,
                             @Part MultipartBody.Part logo);
    @Multipart
    @PUT("/chats/groupChat")
    Call<Chat> putGroupChat(@Part("chat") RequestBody postGroupChat,
                             @Part List<MultipartBody.Part> logo);

    @DELETE("/chats/{chatId}")
    Call<ServerResponse> deleteChat(@Path("chatId") long chatId);
}
