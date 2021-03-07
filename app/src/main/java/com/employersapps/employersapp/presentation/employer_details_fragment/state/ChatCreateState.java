package com.employersapps.employersapp.presentation.employer_details_fragment.state;

import com.employersapps.core.domain.Chat;
import com.employersapps.core.state.ErrorState;
import com.employersapps.core.state.SuccessState;

public abstract class ChatCreateState {

    public static class Success extends SuccessState<Chat>
            implements EmployerDetailsState {

        public Success(Chat result) {
            super(result);
        }
    }

    public static class Error extends ErrorState
        implements EmployerDetailsState {

        public Error(Throwable throwable) {
            super(throwable);
        }
    }
}
