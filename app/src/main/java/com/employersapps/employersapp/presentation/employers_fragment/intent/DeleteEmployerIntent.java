package com.employersapps.employersapp.presentation.employers_fragment.intent;

import com.employersapps.core.domain.Employer;

public class DeleteEmployerIntent implements EmployersFragmentIntent {
    public final Employer toDelete;

    public DeleteEmployerIntent(Employer toDelete) {
        this.toDelete = toDelete;
    }
}
