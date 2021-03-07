package com.employersapps.core.domain;

import java.time.LocalDateTime;

public class AdminNotification {

    private final int targetEmployerId;
    private final String message;
    private final LocalDateTime fireDateTime;

    public AdminNotification(int targetEmployerId, String message, LocalDateTime fireDateTime) {
        this.targetEmployerId = targetEmployerId;
        this.message = message;
        this.fireDateTime = fireDateTime;
    }

    public int getTargetEmployerId() {
        return targetEmployerId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getFireDateTime() {
        return fireDateTime;
    }
}
