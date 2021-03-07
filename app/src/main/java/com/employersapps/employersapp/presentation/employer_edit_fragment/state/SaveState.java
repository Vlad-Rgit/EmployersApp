package com.employersapps.employersapp.presentation.employer_edit_fragment.state;

import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.state.ErrorState;
import com.employersapps.core.state.SuccessState;

public abstract  class SaveState {
    public static class Succes extends SuccessState<ServerResponse>
        implements EmployerEditState{
        public Succes(ServerResponse result) {
            super(result);
        }
    }
    public static class Error extends ErrorState
            implements EmployerEditState{
        public Error(Throwable throwable) {
            super(throwable);
        }
    }
}
