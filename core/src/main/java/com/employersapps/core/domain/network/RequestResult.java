package com.employersapps.core.domain.network;

public class RequestResult<T> {

    private final T result;
    private final boolean isSuccesfull;
    private final ServerResponse serverResponse;

    public RequestResult(T result) {
        this.result = result;
        serverResponse = null;
        isSuccesfull = true;
    }

    public RequestResult(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
        result = null;
        isSuccesfull = false;
    }

    public T getResult() {
        return result;
    }

    public boolean isSuccesfull() {
        return isSuccesfull;
    }

    public ServerResponse getServerResponse() {
        return serverResponse;
    }
}
