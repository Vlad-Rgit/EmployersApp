package com.employersapps.employersapp.presentation.group_chat_details.models;

import android.net.Uri;

public class ChatData {

    private final long id;
    private final String name;
    private final String uri;
    private final boolean allowWriteOnlyForAdmin;

    public ChatData(long id, String name, String uri, boolean allowWriteOnlyForAdmin) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.allowWriteOnlyForAdmin = allowWriteOnlyForAdmin;
    }

    public ChatData(String name, String uri, boolean allowWriteOnlyForAdmin) {
        this.id = 0;
        this.name = name;
        this.uri = uri;
        this.allowWriteOnlyForAdmin = allowWriteOnlyForAdmin;
    }

    public long getId() {
        return id;
    }

    public boolean isAllowWriteOnlyForAdmin() {
        return allowWriteOnlyForAdmin;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }
}
