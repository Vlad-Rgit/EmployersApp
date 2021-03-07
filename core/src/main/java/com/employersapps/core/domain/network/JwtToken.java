package com.employersapps.core.domain.network;

public class JwtToken {

    private final String token;

    public JwtToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
