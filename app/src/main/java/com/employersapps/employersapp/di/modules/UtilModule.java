package com.employersapps.employersapp.di.modules;

import com.employersapps.core.data.SaveAuthHelper;
import com.employersapps.core.utils.NotificationHelper;
import com.employersapps.core.utils.SessionManager;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.employersapps.employersapp.framework.broadcast_receviers.NotificationReceiver;
import com.employersapps.employersapp.framework.data.JwtAuthInterceptor;
import com.employersapps.employersapp.framework.util.AndroidNotificationHelper;
import com.employersapps.employersapp.framework.util.AndroidSessionManager;
import com.employersapps.employersapp.framework.util.SharedPrefsSaveAuthHelper;

@Module
public abstract class UtilModule {
    @Binds
    abstract SessionManager bindSessionManager(AndroidSessionManager impl);

    @Binds
    abstract SaveAuthHelper bindSaveAuthHelper(SharedPrefsSaveAuthHelper impl);

    @Binds
    abstract NotificationHelper bindNotificationHelper(AndroidNotificationHelper impl);
}
