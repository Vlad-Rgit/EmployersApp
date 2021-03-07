package com.employersapps.core.domain;

public class NewsFile implements ListItem<NewsFile>{

    private final String name;
    private final String uriString;
    private final boolean isNetwork;

    public NewsFile(String name, String uriString, boolean isNetwork) {
        this.name = name;
        this.uriString = uriString;
        this.isNetwork = isNetwork;
    }

    public NewsFile(String name, String uriString) {
        this(name, uriString, false);
    }


    public String getName() {
        return name;
    }

    public String getUriString() {
        return uriString;
    }

    public boolean isNetwork() {
        return isNetwork;
    }

    @Override
    public boolean areItemsTheSame(NewsFile other) {
        return name.equals(other.name);
    }

    @Override
    public boolean areContentsTheSame(NewsFile other) {
        return name.equals(other.name);
    }
}
