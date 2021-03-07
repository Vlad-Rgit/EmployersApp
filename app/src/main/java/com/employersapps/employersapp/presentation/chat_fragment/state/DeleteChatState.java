package com.employersapps.employersapp.presentation.chat_fragment.state;

import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.state.SuccessState;

public abstract class DeleteChatState {

    public static class Success extends SuccessState<ServerResponse>
        implements ChatFragmentState {
        public Success(ServerResponse result) {
            super(result);
        }
    }

    public static class Error extends ErrorState
        implements ChatFragmentState {
        public Error(Throwable throwable) {
            super(throwable);
        }
    }
}
