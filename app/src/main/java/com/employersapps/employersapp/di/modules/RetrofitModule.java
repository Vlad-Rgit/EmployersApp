package com.employersapps.employersapp.di.modules;

import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.framework.data.JwtAuthInterceptor;
import com.employersapps.employersapp.framework.data.RetrofitChatService;
import com.employersapps.employersapp.framework.data.RetrofitEmployersService;
import com.employersapps.employersapp.framework.data.RetrofitFmsTokenService;
import com.employersapps.employersapp.framework.data.RetrofitMessagesService;
import com.employersapps.employersapp.framework.data.RetrofitNewsService;
import com.employersapps.employersapp.framework.data.RetrofitNotificationsService;
import com.employersapps.employersapp.framework.data.RetrofitPostsService;
import com.employersapps.employersapp.framework.data.RetrofitRolesService;
import com.employersapps.employersapp.framework.data.RetrofitUserCoords;
import com.employersapps.employersapp.framework.data.RetrofitUserService;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(JwtAuthInterceptor jwtAuthInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(jwtAuthInterceptor)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofitService(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(Config.API_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }



    @Provides
    @Singleton
    public RetrofitUserService provideRetrofitUserService(Retrofit retrofit) {
        return retrofit.create(RetrofitUserService.class);
    }

    @Provides
    @Singleton
    public RetrofitNewsService provideRetrofitNewsService(Retrofit retrofit) {
        return retrofit.create(RetrofitNewsService.class);
    }

    @Provides
    @Singleton
    public RetrofitEmployersService provideRetrofitEmployersService(Retrofit retrofit) {
        return retrofit.create(RetrofitEmployersService.class);
    }

    @Provides
    @Singleton
    public RetrofitUserCoords provideUserCooords(Retrofit retrofit) {
        return retrofit.create(RetrofitUserCoords.class);
    }

    @Provides
    @Singleton
    public RetrofitMessagesService provideMessagesDataSource(Retrofit retrofit) {
        return retrofit.create(RetrofitMessagesService.class);
    }

    @Provides
    @Singleton
    public RetrofitFmsTokenService provideFmsTokenService(Retrofit retrofit) {
        return retrofit.create(RetrofitFmsTokenService.class);
    }

    @Provides
    @Singleton
    public RetrofitRolesService provideRolesService(Retrofit retrofit) {
        return retrofit.create(RetrofitRolesService.class);
    }

    @Provides
    @Singleton
    public RetrofitPostsService providePostsService(Retrofit retrofit) {
        return retrofit.create(RetrofitPostsService.class);
    }

    @Provides
    @Singleton
    public RetrofitNotificationsService provideNotificationService(Retrofit retrofit) {
        return retrofit.create(RetrofitNotificationsService.class);
    }

    @Provides
    @Singleton
    public RetrofitChatService provideChatService(Retrofit retrofit) {
        return retrofit.create(RetrofitChatService.class);
    }
}
