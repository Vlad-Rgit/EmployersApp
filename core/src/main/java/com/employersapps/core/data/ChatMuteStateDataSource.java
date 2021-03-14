package com.employersapps.core.data;


import com.employersapps.core.domain.ChatMuteState;
import com.employersapps.core.domain.network.PostMuteState;
import com.employersapps.core.utils.Deferrable;

public interface ChatMuteStateDataSource {
    Deferrable<ChatMuteState> getState(long chatId, long userId);
    Deferrable<PostMuteState> updateState(PostMuteState postMuteState);
}
