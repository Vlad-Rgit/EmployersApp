package com.employersapps.employersapp.presentation.chat_fragment.intent;

import com.employersapps.core.domain.Message;

public class AddMessageIntent implements ChatFragmentIntent {
    private final String text;

    public AddMessageIntent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
