package com.employersapps.employersapp.framework.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.MessageAttachment;

import java.time.LocalDateTime;
import java.util.List;

@Entity(tableName = "marked_messages")
public class MarkedMessageEntity {


    @PrimaryKey
    private final long id;
    private final long senderId;
    private final long chatId;
    private final String text;
    private final String sender;
    private final String logoPath;
    private final long timestamp;
    private final String chatName;
    private final Long firstReadTimestamp;
    private final boolean isPrivate;

    public MarkedMessageEntity(long id,
                               long senderId,
                               long chatId,
                               String text,
                               String sender,
                               String logoPath,
                               long timestamp,
                               String chatName,
                               Long firstReadTimestamp,
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

    public long getTimestamp() {
        return timestamp;
    }

    public String getChatName() {
        return chatName;
    }

    public Long getFirstReadTimestamp() {
        return firstReadTimestamp;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
