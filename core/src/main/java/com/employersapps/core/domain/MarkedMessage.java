package com.employersapps.core.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class MarkedMessage implements ListItem<MarkedMessage> {

    private final long id;
    private final long senderId;
    private final long chatId;
    private final String text;
    private final String sender;
    private final String logoPath;
    private final LocalDateTime timestamp;
    private final String chatName;
    private final LocalDateTime firstReadTimestamp;
    private final boolean isPrivate;

    public MarkedMessage(long id,
                         long senderId,
                         long chatId,
                         String text,
                         String sender,
                         String logoPath,
                         LocalDateTime timestamp,
                         String chatName,
                         LocalDateTime firstReadTimestamp,
                         boolean isPrivate) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.text = text;
        this.sender = sender;
        this.logoPath = logoPath;
        this.timestamp = timestamp;
        this.chatName = chatName;
        this.firstReadTimestamp = firstReadTimestamp;
        this.isPrivate = isPrivate;
    }


    public long getId() {
        return id;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getChatName() {
        return chatName;
    }

    public LocalDateTime getFirstReadTimestamp() {
        return firstReadTimestamp;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkedMessage that = (MarkedMessage) o;
        return id == that.id && senderId == that.senderId && chatId == that.chatId && isPrivate == that.isPrivate && Objects.equals(text, that.text) && Objects.equals(sender, that.sender) && Objects.equals(logoPath, that.logoPath) && Objects.equals(timestamp, that.timestamp) && Objects.equals(chatName, that.chatName) && Objects.equals(firstReadTimestamp, that.firstReadTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                senderId,
                chatId,
                text,
                sender,
                logoPath,
                timestamp,
                chatName,
                firstReadTimestamp,
                isPrivate);
    }

    @Override
    public boolean areItemsTheSame(MarkedMessage other) {
        return id == other.getId();
    }

    @Override
    public boolean areContentsTheSame(MarkedMessage other) {
        return equals(other);
    }
}
