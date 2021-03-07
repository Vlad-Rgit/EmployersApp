package com.employersapps.employersapp.presentation.messages_fragment.state;

import com.employersapps.core.domain.LastMessageChat;
import com.employersapps.core.domain.Message;
import com.employersapps.core.state.ErrorState;
import com.employersapps.core.state.SuccessState;
import com.employersapps.employersapp.presentation.messages_fragment.intent.MessagesFragmentIntent;

import java.util.List;

public abstract class ListState {
    public static class Success extends SuccessState<List<LastMessageChat>>
     implements MessagesFragmentState {

        public Success(List<LastMessageChat> result) {
            super(result);
        }
    }
    public static class Error extends ErrorState
        implements MessagesFragmentState {
        public Error(Throwable throwable) {
            super(throwable);
        }
    }
}
