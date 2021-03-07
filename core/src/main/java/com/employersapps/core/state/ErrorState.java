package com.employersapps.core.state;

public class ErrorState {

    private final Throwable throwable;

    public ErrorState(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
