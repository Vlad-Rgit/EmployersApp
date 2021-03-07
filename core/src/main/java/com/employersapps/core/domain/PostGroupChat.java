package com.employersapps.core.domain;

import java.util.List;

public class PostGroupChat {

    private final String name;
    private final String logoUri;
    private final boolean allowWriteOnlyAdmin;
    private final List<Long> usersInChat;
    private final long creatorId;

    public PostGroupChat(String name,
                         String logoUri,
                         boolean allowWriteOnlyAdmin,
                         List<Long> usersInChat,
                         long creatorId) {
        this.name = name;
        this.logoUri = logoUri;
        this.usersInChat = usersInChat;
        this.allowWriteOnlyAdmin = allowWriteOnlyAdmin;
        this.creatorId = creatorId;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public boolean isAllowWriteOnlyAdmin() {
        return allowWriteOnlyAdmin;
    }

    public String getName() {
        return name;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public List<Long> getUsersInChat() {
        return usersInChat;
    }
}
