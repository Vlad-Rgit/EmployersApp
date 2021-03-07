package com.employersapps.employersapp.presentation.employer_edit_fragment.intent;

import com.employersapps.core.domain.network.PostEmployer;

public class AddEmployerIntent implements EmployerEditIntent{
    public final PostEmployer postEmployer;
    public final String photoUri;

    public AddEmployerIntent(PostEmployer postEmployer, String photoUri) {
        this.postEmployer = postEmployer;
        this.photoUri = photoUri;
    }
}
