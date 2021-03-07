package com.employersapps.core.domain;



import java.time.LocalDateTime;
import java.util.Objects;

public class LastMessageChat implements ListItem<LastMessageChat> {

    private final long id;
    private final String name;
    private final String logoPath;
    private final int senderId;
    private final String senderFirstName;
    private final String senderLastName;
    private final String senderPhotoPath;
    private final Integer receiverId;
    private final String receiverFirstName;
    private final String receiverLastName;
    private final String receiverPhotoPath;
    private final String text;
    private final LocalDateTime timestamp;
    private final boolean isInPrivateChat;

    public LastMessageChat(long id,
                           String name,
                           String logoPath,
                           int senderId,
                           String senderFirstName,
                           String senderLastName,
                           String senderPhotoPath,
                           Integer receiverId,
                           String receiverFirstName,
                           String receiverLastName,
                           String receiverPhotoPath,
                           String text,
                           LocalDateTime timestamp,
                           boolean isInPrivateChat) {
        this.id = id;
        this.name = name;
        this.logoPath = logoPath;
        this.senderId = senderId;
        this.senderFirstName = senderFirstName;
        this.senderLastName = senderLastName;
        this.senderPhotoPath = senderPhotoPath;
        this.receiverId = receiverId;
        this.receiverFirstName = receiverFirstName;
        this.receiverLastName = receiverLastName;
        this.receiverPhotoPath = receiverPhotoPath;
        this.text = text;
        this.timestamp = timestamp;
        this.isInPrivateChat = isInPrivateChat;
    }

    public String getReceiverFirstName() {
        return receiverFirstName;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public String getReceiverLastName() {
        return receiverLastName;
    }

    public String getReceiverPhotoPath() {
        return receiverPhotoPath;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderPhotoPath() {
        return senderPhotoPath;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isInPrivateChat() {
        return isInPrivateChat;
    }

    @Override
    public boolean areItemsTheSame(LastMessageChat other) {
        return getId() == other.getId();
    }

    @Override
    public boolean areContentsTheSame(LastMessageChat other) {
        return equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LastMessageChat that = (LastMessageChat) o;
        return id == that.id && Objects.equals(name, that.name) &&
                Objects.equals(logoPath, that.logoPath) &&
                Objects.equals(senderId, that.senderId) &&
                Objects.equals(senderFirstName, that.senderFirstName) &&
                Objects.equals(senderLastName, that.senderLastName) &&
                Objects.equals(text, that.text) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                name,
                logoPath,
                senderId,
                senderFirstName,
                senderLastName,
                text,
                timestamp);
    }
}
