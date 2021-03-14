package com.employersapps.employersapp.framework.data;

import com.employersapps.core.data.ChatMuteStateDataSource;
import com.employersapps.core.domain.ChatMuteState;
import com.employersapps.core.domain.network.PostMuteState;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.framework.util.DeferrableCallback;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class ApiChatMuteStateDataSource implements ChatMuteStateDataSource {

    private final RetrofitChatMuteStateService chatMuteStateService;

    @Inject
    public ApiChatMuteStateDataSource(RetrofitChatMuteStateService chatMuteStateService) {
        this.chatMuteStateService = chatMuteStateService;
    }

    @Override
    public Deferrable<ChatMuteState> getState(long chatId, long userId) {
        DeferrableCallback<ChatMuteState> callback = new DeferrableCallback<>();
        chatMuteStateService.getChatMuteState(userId, chatId).enqueue(callback);
        return callback.getDeferrable();
    }

    @Override
    public Deferrable<PostMuteState> updateState(PostMuteState postMuteState) {
        DeferrableCallback<PostMuteState> callback = new DeferrableCallback<>();
        chatMuteStateService.putChatMuteState(postMuteState).enqueue(callback);
        return callback.getDeferrable();
    }
}
