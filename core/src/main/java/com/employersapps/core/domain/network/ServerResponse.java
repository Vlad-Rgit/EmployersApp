package com.employersapps.core.domain.network;

public class ServerResponse {

    private final String message;
    private final int status;

    public ServerResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
