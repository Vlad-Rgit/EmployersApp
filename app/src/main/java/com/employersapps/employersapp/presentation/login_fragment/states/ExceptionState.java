package com.employersapps.employersapp.presentation.login_fragment.states;


public class ExceptionState implements LoginFragmentState {

    private final Throwable t;

    public ExceptionState(Throwable t) {
        this.t = t;
    }

    public Throwable getThrowable() {
        return t;
    }
}
