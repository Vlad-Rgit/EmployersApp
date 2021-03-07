package com.employersapps.employersapp.presentation.group_chat_details.models;

import com.employersapps.core.domain.Chat;
import com.employersapps.core.state.ErrorState;
import com.employersapps.core.state.SuccessState;

public abstract class ResultData {
    public static class Success extends SuccessState<Chat>
     implements Result {
        public Success(Chat result) {
            super(result);
        }
    }

    public static class Error extends ErrorState
        implements Result {
        public Error(Throwable throwable) {
            super(throwable);
        }
    }

    public static class NoResult implements Result {

    }
}
