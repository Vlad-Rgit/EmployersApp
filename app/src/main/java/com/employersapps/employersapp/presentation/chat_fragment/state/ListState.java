package com.employersapps.employersapp.presentation.chat_fragment.state;

import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.Message;

import java.util.List;

public class ListState implements ChatFragmentState {
    private final List<Message> messages;
    private final Employer receiver;

    public ListState(List<Message> messages, Employer receiver) {
        this.messages = messages;
        this.receiver = receiver;
    }

    public Employer getReceiver() {
        return receiver;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
