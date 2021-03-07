package com.employersapps.core.data;

import com.employersapps.core.domain.LastMessageChat;
import com.employersapps.core.domain.Message;
import com.employersapps.core.domain.MessageAttachment;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;

import java.util.List;

public interface MessagesDataSource {
    Deferrable<List<Message>> getMessages();
    Deferrable<List<LastMessageChat>> getChats(long userId);
    Deferrable<ServerResponse> addMessage(Message message, List<MessageAttachment> attachments);
}
