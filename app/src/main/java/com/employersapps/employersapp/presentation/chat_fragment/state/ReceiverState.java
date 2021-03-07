package com.employersapps.employersapp.presentation.chat_fragment.state;

import com.employersapps.core.domain.Employer;

public class ReceiverState implements ChatFragmentState {
    private final Employer receiver;

    public ReceiverState(Employer receiver) {
        this.receiver = receiver;
    }

    public Employer getReceiver() {
        return receiver;
    }
}
