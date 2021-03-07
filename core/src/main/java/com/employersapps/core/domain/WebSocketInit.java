package com.employersapps.core.domain;

public class WebSocketInit {
    private final String type;
    private final long userId;

    public WebSocketInit(long userId) {
        this.type = "init";
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public long getUserId() {
        return userId;
    }
}
