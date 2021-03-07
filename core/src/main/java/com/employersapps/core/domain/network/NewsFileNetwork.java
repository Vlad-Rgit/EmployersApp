package com.employersapps.core.domain.network;

import java.io.Serializable;

public class NewsFileNetwork implements Serializable {

    private final String name;

    public NewsFileNetwork(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getOriginalFileName() {
        return name.substring(name.indexOf('-') + 1);
    }
}
