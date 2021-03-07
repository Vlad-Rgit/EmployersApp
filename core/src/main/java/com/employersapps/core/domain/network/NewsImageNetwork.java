package com.employersapps.core.domain.network;

import java.io.Serializable;

public class NewsImageNetwork implements Serializable {

    private final String name;

    public NewsImageNetwork(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
