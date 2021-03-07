package com.employersapps.employersapp.presentation.chat_fragment.intent;

import com.employersapps.core.domain.MessageAttachment;

public class RemoveAttachmentIntent implements ChatFragmentIntent {
    private final MessageAttachment messageAttachment;

    public RemoveAttachmentIntent(MessageAttachment messageAttachment) {
        this.messageAttachment = messageAttachment;
    }

    public MessageAttachment getMessageAttachment() {
        return messageAttachment;
    }
}
