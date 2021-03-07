package com.employersapps.employersapp.presentation.login_fragment.states;

import com.employersapps.core.domain.network.ServerResponse;

public class UnknownErrorState implements LoginFragmentState {
    private final ServerResponse serverResponse;

    public UnknownErrorState(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }

    public ServerResponse getServerResponse() {
        return serverResponse;
    }
}
