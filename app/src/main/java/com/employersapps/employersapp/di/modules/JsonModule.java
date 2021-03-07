package com.employersapps.employersapp.di.modules;

import com.employersapps.employersapp.framework.serialization.LocalDateAdapter;
import com.employersapps.employersapp.framework.serialization.LocalDateTimeAdapter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class JsonModule {

    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

}
