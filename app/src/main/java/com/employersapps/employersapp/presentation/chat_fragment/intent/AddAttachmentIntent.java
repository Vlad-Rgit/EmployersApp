package com.employersapps.employersapp.presentation.chat_fragment.intent;

import com.employersapps.core.domain.MessageAttachment;

public class AddAttachmentIntent implements ChatFragmentIntent {
    private final MessageAttachment messageAttachment;

    public AddAttachmentIntent(MessageAttachment messageAttachment) {
        this.messageAttachment = messageAttachment;
    }

    public MessageAttachment getMessageAttachment() {
        return messageAttachment;
    }
}
