package com.employersapps.core.utils;

import com.employersapps.core.domain.network.ServerResponse;

public class IllegalRequestException extends Exception {

    private final ServerResponse serverResponse;

    public IllegalRequestException(ServerResponse serverResponse) {
        super(serverResponse.getMessage());
        this.serverResponse = serverResponse;
    }

    public IllegalRequestException(int status, String message) {
        super(message);
        serverResponse = new ServerResponse(message, status);
    }

    public ServerResponse getServerResponse() {
        return serverResponse;
    }
}
