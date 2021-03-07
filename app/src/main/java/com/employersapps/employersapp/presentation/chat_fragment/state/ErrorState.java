package com.employersapps.employersapp.presentation.chat_fragment.state;

public class ErrorState implements ChatFragmentState {
    private final Throwable throwable;

    public ErrorState(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}
