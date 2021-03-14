package com.employersapps.core.domain;

public class ChatMuteState {

    private final boolean state;

    public ChatMuteState(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }
}
