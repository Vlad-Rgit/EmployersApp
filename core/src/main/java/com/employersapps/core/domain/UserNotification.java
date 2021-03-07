package com.employersapps.core.domain;


import java.time.LocalDateTime;


public class UserNotification implements ListItem<UserNotification> {

    private final long id;
    private final String text;
    private final LocalDateTime fireDateTime;
    private final boolean isFired;

    public UserNotification(long id, String text, LocalDateTime fireDateTime, boolean isFired) {
        this.id = id;
        this.text = text;
        this.fireDateTime = fireDateTime;
        this.isFired = isFired;
    }

    public UserNotification(String text, LocalDateTime fireDateTime, boolean isFired) {
        this(0, text, fireDateTime, isFired);
    }


    public UserNotification(String text, LocalDateTime fireDateTime) {
        this(text, fireDateTime, false);
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getFireDateTime() {
        return fireDateTime;
    }

    public boolean getIsFired() {
        return isFired;
    }

    @Override
    public boolean areItemsTheSame(UserNotification other) {
        return this.id == other.id;
    }

    @Override
    public boolean areContentsTheSame(UserNotification other) {
        return this.text.equals(other.text);
    }
}
