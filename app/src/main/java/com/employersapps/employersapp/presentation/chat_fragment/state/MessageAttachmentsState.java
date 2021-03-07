package com.employersapps.employersapp.presentation.chat_fragment.state;

import com.employersapps.core.domain.MessageAttachment;

import java.util.List;

public class MessageAttachmentsState implements ChatFragmentState{

    private final List<MessageAttachment> attachments;

    public MessageAttachmentsState(List<MessageAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<MessageAttachment> getAttachments() {
        return attachments;
    }
}
