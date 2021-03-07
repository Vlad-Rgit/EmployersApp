package com.employersapps.core.domain;

public class MessagesInit {

    private final String type;
    private final long chatId;
    private final long userId;

    public MessagesInit(String type, long chatId, long userId) {
        this.type = type;
        this.chatId = chatId;
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public long getChatId() {
        return chatId;
    }
}
