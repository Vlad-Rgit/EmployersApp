package com.employersapps.core.state;

public class SuccessState<T> {

    private final T result;

    public SuccessState(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
