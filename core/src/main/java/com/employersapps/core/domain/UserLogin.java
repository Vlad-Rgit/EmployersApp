package com.employersapps.core.domain;

public class UserLogin {

    private final String login;
    private final String password;

    public UserLogin(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }

}
