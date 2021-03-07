package com.employersapps.employersapp.framework.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.employersapps.core.utils.SessionManager;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AndroidSessionManager implements SessionManager {

    private final static String KEY_JWT_TOKEN = "keyJwtToken";

    private final Context context;

    private String jwtToken = null;

    @Inject
    public AndroidSessionManager(Context context) {
        this.context = context;
    }

    @Override
    public void setJwtToken(String token) {

        SharedPreferences prefs = SharedPrefsHelper
                .getCommonSharedPreferences(context);

        jwtToken = token;

        prefs.edit()
                .putString(KEY_JWT_TOKEN, token)
                .apply();
    }

    @Override
    public @Nullable String getJwtToken() {

        if(jwtToken != null) {
            return jwtToken;
        }

        SharedPreferences prefs = SharedPrefsHelper
                .getCommonSharedPreferences(context);

        jwtToken = prefs.getString(
                KEY_JWT_TOKEN,
                null
        );

        return jwtToken;
    }
}
