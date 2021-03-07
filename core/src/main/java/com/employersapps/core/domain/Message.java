package com.employersapps.core.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Message implements ListItem<Message> {

    private final long id;
    private final long senderId;
    private final long chatId;
    private final String text;
    private final Employer sender;
    private final LocalDateTime timestamp;
    private final List<MessageAttachment> messageAttachments;
    private final Chat chat;
    private final LocalDateTime firstReadTimestamp;

    private boolean isSending = false;

    public Message(long id,
                   long senderId,
                   long chatId,
                   String text,
                   Employer sender,
                   LocalDateTime timestamp,
                   List<MessageAttachment> messageAttachments,
                   Chat chat,
                   LocalDateTime firstReadTimestamp) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.text = text;
        this.sender = sender;
        this.timestamp = timestamp;
        this.messageAttachments = messageAttachments;
        this.chat = chat;
        this.firstReadTimestamp = firstReadTimestamp;
    }

    public Message(long senderId,
                   long chatId,
                   String text,
                   List<MessageAttachment> messageAttachments) {
        this(0,
                senderId,
                chatId,
                text,
                null,
                LocalDateTime.now(),
                messageAttachments,
                null,
                null);
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Employer getSender() {
        return sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<MessageAttachment> getMessageAttachments() {
        return messageAttachments == null ? Collections.emptyList() : messageAttachments;
    }

    public LocalDateTime getFirstReadTimestamp() {
        return firstReadTimestamp;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getChatId() {
        return chatId;
    }

    public Chat getChat() {
        return chat;
    }

    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean sending) {
        isSending = sending;
    }

    @Override
    public boolean areItemsTheSame(Message other) {
        return id == other.id;
    }

    @Override
    public boolean areContentsTheSame(Message other) {
        return text.equals(other.text);
    }
}
