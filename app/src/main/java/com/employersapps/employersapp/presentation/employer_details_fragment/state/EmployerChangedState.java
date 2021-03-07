package com.employersapps.employersapp.presentation.employer_details_fragment.state;

import com.employersapps.core.domain.Employer;
import com.employersapps.core.state.ErrorState;
import com.employersapps.core.state.SuccessState;

public abstract class EmployerChangedState {
    public static class Success extends SuccessState<Employer>
        implements EmployerDetailsState {
        public Success(Employer result) {
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
