package com.employersapps.core.data;

public interface SaveAuthHelper {
    boolean isAuthenticated();
    void setLogin(String login);
    void setPassword(String password);
    String getLogin();
    String getPassword();
}
