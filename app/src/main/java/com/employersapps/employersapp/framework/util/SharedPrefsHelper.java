package com.employersapps.employersapp.framework.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.employersapps.employersapp.config.Config;

public class SharedPrefsHelper {

    public static SharedPreferences getCommonSharedPreferences(Context context) {
        return context.getSharedPreferences(
                Config.SHARED_PREFS_NAME_COMMON,
                Context.MODE_PRIVATE
        );
    }

}
