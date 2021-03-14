package com.employersapps.employersapp.framework.data;

import com.employersapps.core.domain.ChatMuteState;
import com.employersapps.core.domain.network.PostMuteState;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RetrofitChatMuteStateService {

    @GET("/muteState")
    Call<ChatMuteState> getChatMuteState(
            @Query("user_id") long userId,
            @Query("chat_id") long chatId
    );

    @PUT("/muteState")
    Call<PostMuteState> putChatMuteState(
            @Body PostMuteState postMuteState
    );

}
