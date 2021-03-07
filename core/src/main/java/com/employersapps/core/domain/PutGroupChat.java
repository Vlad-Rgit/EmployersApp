package com.employersapps.core.domain;

import java.util.List;

public class PutGroupChat {

    private final long id;
    private final String name;
    private final String logoUri;
    private final boolean allowWriteOnlyAdmin;
    private final List<Long> usersInChat;

    public PutGroupChat(long id,
                        String name,
                        String logoUri,
                        boolean allowWriteOnlyAdmin,
                        List<Long> usersInChat) {
        this.id = id;
        this.name = name;
        this.logoUri = logoUri;
        this.allowWriteOnlyAdmin = allowWriteOnlyAdmin;
        this.usersInChat = usersInChat;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public boolean isAllowWriteOnlyAdmin() {
        return allowWriteOnlyAdmin;
    }

    public List<Long> getUsersInChat() {
        return usersInChat;
    }
}
