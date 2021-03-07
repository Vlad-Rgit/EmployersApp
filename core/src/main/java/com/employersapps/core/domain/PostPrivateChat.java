package com.employersapps.core.domain;

public class PostPrivateChat {

    private final long senderId;
    private final long receiverId;


    public PostPrivateChat(long senderId, long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public long getSenderId() {
        return senderId;
    }
}
