package com.employersapps.employersapp.framework.data;

import android.util.Log;

import com.employersapps.core.data.SaveAuthHelper;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.domain.network.LoginResponse;
import com.employersapps.core.domain.network.RequestResult;
import com.employersapps.core.domain.User;
import com.employersapps.core.domain.UserLogin;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.SessionManager;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ApiUserDataSource implements UserDataSource {

    private User currentUser = null;
    private final RetrofitUserService userService;
    private final Gson gson;
    private final SessionManager sessionManager;
    private final SaveAuthHelper saveAuthHelper;

    @Inject
    public ApiUserDataSource(RetrofitUserService userService,
                             Gson gson,
                             SessionManager sessionManager,
                             SaveAuthHelper saveAuthHelper) {
        this.userService = userService;
        this.gson = gson;
        this.sessionManager = sessionManager;
        this.saveAuthHelper = saveAuthHelper;
    }

    @NotNull
    @Override
    public Observable<List<User>> getUserListObservable() {
        return null;
    }

    @Override
    public @NotNull User getCurrentUser() {
        if(currentUser == null) {
            throw new IllegalStateException("User is not authenticated");
        }
        return currentUser;
    }

    @Override
    public boolean isLogged() {
        return currentUser != null;
    }


    @NotNull
    @Override
    public Deferrable<RequestResult<User>> login(UserLogin user) {

        Deferrable<RequestResult<User>> deferrable = new Deferrable<>();

        userService.login(user).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()) {

                    LoginResponse loginResponse = response.body();

                    //Save Current User to access it later
                    currentUser = loginResponse.getUser();

                    //Persist login and password
                    saveAuthHelper.setLogin(user.getLogin());
                    saveAuthHelper.setPassword(user.getPassword());

                    //Save jwt token
                    sessionManager.setJwtToken(loginResponse.getToken());

                    RequestResult<User> result = new RequestResult<>(currentUser);
                    deferrable.complete(result);
                }
                else {

                    ServerResponse serverResponse = gson.fromJson(
                            response.errorBody().charStream(),
                            ServerResponse.class
                    );

                    RequestResult<User> result = new RequestResult<>(serverResponse);
                    deferrable.complete(result);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);
                deferrable.completeWithException(t);
            }
        });

        return deferrable;
    }
}
