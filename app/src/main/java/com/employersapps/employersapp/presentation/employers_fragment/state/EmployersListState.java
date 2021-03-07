package com.employersapps.employersapp.presentation.employers_fragment.state;

import com.employersapps.core.domain.Employer;
import com.employersapps.core.state.ErrorState;
import com.employersapps.core.state.SuccessState;

import java.util.List;
import java.util.ListIterator;

public abstract class EmployersListState {

    public static class Success extends SuccessState<List<Employer>>
            implements EmployersFragmentState {
        public Success(List<Employer> employers) {
            super(employers);
        }
    }

    public static class Error extends ErrorState
            implements EmployersFragmentState {

        public Error(Throwable throwable) {
            super(throwable);
        }
    }
}
