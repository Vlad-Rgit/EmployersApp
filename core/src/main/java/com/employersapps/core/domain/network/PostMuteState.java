package com.employersapps.core.domain.network;

public class PostMuteState {

    private final long userId;
    private final long chatId;
    private final boolean enableNotification;

    public PostMuteState(long userId, long chatId, boolean enableNotification) {
        this.userId = userId;
        this.chatId = chatId;
        this.enableNotification = enableNotification;
    }

    public long getUserId() {
        return userId;
    }

    public long getChatId() {
        return chatId;
    }

    public boolean isEnableNotification() {
        return enableNotification;
    }
}
