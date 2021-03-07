package com.employersapps.employersapp.presentation.login_fragment.intents;

import com.employersapps.core.domain.UserLogin;

public class LoginIntent implements LoginFragmentIntent {

    private final UserLogin userLogin;

    public LoginIntent(UserLogin userLogin) {
        this.userLogin = userLogin;
    }

    public UserLogin getUserLogin() {
        return userLogin;
    }
}
