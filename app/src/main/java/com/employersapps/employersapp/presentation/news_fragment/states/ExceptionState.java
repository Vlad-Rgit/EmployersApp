package com.employersapps.employersapp.presentation.news_fragment.states;

public class ExceptionState implements NewsFragmentState {
    private final Throwable throwable;

    public ExceptionState(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
