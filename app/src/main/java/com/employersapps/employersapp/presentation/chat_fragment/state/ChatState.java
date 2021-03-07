package com.employersapps.employersapp.presentation.chat_fragment.state;

import com.employersapps.core.domain.Chat;

public class ChatState implements ChatFragmentState {
    private final Chat chat;

    public ChatState(Chat chat) {
        this.chat = chat;
    }

    public Chat getChat() {
        return chat;
    }
}
