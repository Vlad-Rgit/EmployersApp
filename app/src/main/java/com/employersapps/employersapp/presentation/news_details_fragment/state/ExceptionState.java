package com.employersapps.employersapp.presentation.news_details_fragment.state;

public class ExceptionState implements NewsDetailsState {

    private final Throwable throwable;

    public ExceptionState(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
