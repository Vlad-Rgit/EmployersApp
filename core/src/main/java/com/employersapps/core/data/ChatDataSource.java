package com.employersapps.core.data;

import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.PostGroupChat;
import com.employersapps.core.domain.PostPrivateChat;
import com.employersapps.core.domain.PutGroupChat;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;

public interface ChatDataSource {
    Deferrable<Chat> getChatDetails(long id);
    Deferrable<Chat> addPrivateChat(PostPrivateChat postPrivateChat);
    Deferrable<Chat> addGroupChat(PostGroupChat postGroupChat);
    Deferrable<Chat> updateGroupChat(PutGroupChat putGroupChat);
    Deferrable<ServerResponse> deleteChat(Chat chat);
}
