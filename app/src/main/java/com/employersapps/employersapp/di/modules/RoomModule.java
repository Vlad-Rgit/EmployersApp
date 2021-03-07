package com.employersapps.employersapp.di.modules;

import android.content.Context;

import com.employersapps.employersapp.framework.database.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(Context context) {
        return AppDatabase.getInstance(context);
    }

}
