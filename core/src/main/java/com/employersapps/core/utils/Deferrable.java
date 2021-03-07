package com.employersapps.core.utils;

public class Deferrable<T> {

    public interface OnCompleteCallback<T> {
        void onComplete(T result);
    }

    public interface OnExceptionCallback<T> {
        void onException(Throwable t);
    }

    private OnCompleteCallback<T> onCompleteCallback;
    private OnExceptionCallback<T> onExceptionCallback;

    private T result;


    private void provideResult() {
        this.onCompleteCallback.onComplete(result);
        result = null;
    }

    public Deferrable<T> onComplete(OnCompleteCallback<T> onCompleteCallback) {
        this.onCompleteCallback = onCompleteCallback;
        if(result != null) {
            provideResult();
        }
        return this;
    }

    public Deferrable<T> onException(OnExceptionCallback<T> onExceptionCallback) {
        this.onExceptionCallback = onExceptionCallback;
        return this;
    }


    public void complete(T result) {
        this.result = result;
        if(onCompleteCallback != null) {
            provideResult();
        }
    }

    public void completeWithException(Throwable t) {
        if (onExceptionCallback != null) {
            onExceptionCallback.onException(t);
        }
    }

}
