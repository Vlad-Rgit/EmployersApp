package com.employersapps.core.domain;

import java.io.Serializable;
import java.util.List;

public class Chat implements Serializable {

    private final long id;
    private final String name;
    private final String logoPath;
    private final boolean isPrivate;
    private final List<Employer> users;
    private final boolean allowWriteOnlyAdmin;
    private final long creatorId;

    public Chat(long id,
                String name,
                String logo_path,
                List<Employer> users,
                boolean isPrivate,
                boolean allowWriteOnlyAdmin,
                long creatorId) {
        this.id = id;
        this.name = name;
        this.logoPath = logo_path;
        this.users = users;
        this.isPrivate = isPrivate;
        this.allowWriteOnlyAdmin = allowWriteOnlyAdmin;
        this.creatorId = creatorId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public List<Employer> getUsers() {
        return users;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isAllowWriteOnlyAdmin() {
        return allowWriteOnlyAdmin;
    }
}
