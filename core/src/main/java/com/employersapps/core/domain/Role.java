package com.employersapps.core.domain;

public class Role {

    public final int id;
    public final String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
