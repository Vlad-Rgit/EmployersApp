package com.employersapps.employersapp.presentation.login_fragment.states;

import com.employersapps.core.domain.User;

public class LoggedState implements LoginFragmentState {

    private final User user;

    public LoggedState(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
