package com.employersapps.core.utils;

public class IntentIsNotSupportedException extends RuntimeException{
    public IntentIsNotSupportedException(String intentName) {
        super(intentName + " is not supported");
    }
}
