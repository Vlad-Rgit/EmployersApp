package com.employersapps.core.utils;

public class StateIsNotSupported extends RuntimeException {
    public StateIsNotSupported(String stateName) {
        super(stateName + " is not supported");
    }
}
