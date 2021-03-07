package com.employersapps.employersapp.framework.data;

import com.employersapps.core.utils.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class JwtAuthInterceptor implements Interceptor {

    private SessionManager sessionManager;

    @Inject
    public JwtAuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    @Override
    public @NotNull Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();

        String jwtToken = sessionManager.getJwtToken();

        if(jwtToken != null) {
            builder.addHeader("Authorization", "Bearer " + jwtToken);
        }

        return chain.proceed(builder.build());
    }
}
