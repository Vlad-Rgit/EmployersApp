package com.employersapps.core.domain;

import java.util.List;

public class MessageAttachment implements ListItem<MessageAttachment> {

    private final String uriString;
    private final String name;
    private final long size;

    public MessageAttachment(String uriString, String name, long size) {
        this.uriString = uriString;
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getUriString() {
        return uriString;
    }

    public String getShortName() {
        int delimiterIndex = name.indexOf('-');
        if(delimiterIndex > -1) {
            return name.substring(name.indexOf('-') + 1);
        }
        else {
            return name;
        }
    }

    @Override
    public boolean areItemsTheSame(MessageAttachment other) {
        return uriString.equals(other.getUriString());
    }

    @Override
    public boolean areContentsTheSame(MessageAttachment other) {
        return uriString.equals(other.getUriString());
    }
}
