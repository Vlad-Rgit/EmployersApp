package com.employersapps.employersapp.framework.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.employersapps.core.data.SaveAuthHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPrefsSaveAuthHelper implements SaveAuthHelper {

    private static final String KEY_LOGIN = "keyLogin";
    private static final String KEY_PASSWORD = "keyPassword";

    private final Context context;

    @Inject
    public SharedPrefsSaveAuthHelper(Context context) {
        this.context = context;
    }


    @Override
    public boolean isAuthenticated() {
        SharedPreferences prefs = SharedPrefsHelper
                .getCommonSharedPreferences(context);

        return prefs.getString(KEY_LOGIN, null) != null &&
                prefs.getString(KEY_PASSWORD, null) != null;
    }

    @Override
    public void setLogin(String login) {

        SharedPreferences prefs = SharedPrefsHelper
                .getCommonSharedPreferences(context);

        prefs.edit()
                .putString(KEY_LOGIN, login)
                .apply();
    }

    @Override
    public void setPassword(String password) {

        SharedPreferences prefs = SharedPrefsHelper
                .getCommonSharedPreferences(context);

        prefs.edit()
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    @Override
    public String getLogin() {

        SharedPreferences prefs = SharedPrefsHelper
                .getCommonSharedPreferences(context);

        String login = prefs.getString(KEY_LOGIN, null);

        if(login == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        return login;
    }

    @Override
    public String getPassword() {

        SharedPreferences prefs = SharedPrefsHelper
                .getCommonSharedPreferences(context);

        String password = prefs.getString(KEY_PASSWORD, null);

        if(password == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        return password;
    }
}
