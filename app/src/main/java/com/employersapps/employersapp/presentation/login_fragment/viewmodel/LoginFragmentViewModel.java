package com.employersapps.employersapp.presentation.login_fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.domain.network.RequestResult;
import com.employersapps.core.domain.User;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.login_fragment.intents.LoginFragmentIntent;
import com.employersapps.employersapp.presentation.login_fragment.intents.LoginIntent;
import com.employersapps.employersapp.presentation.login_fragment.states.ErrorCredentialsState;
import com.employersapps.employersapp.presentation.login_fragment.states.ExceptionState;
import com.employersapps.employersapp.presentation.login_fragment.states.LoadingState;
import com.employersapps.employersapp.presentation.login_fragment.states.LoggedState;
import com.employersapps.employersapp.presentation.login_fragment.states.LoginFragmentState;
import com.employersapps.employersapp.presentation.login_fragment.states.TimeoutServerState;
import com.employersapps.employersapp.presentation.login_fragment.states.UnknownErrorState;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

public class LoginFragmentViewModel extends AndroidViewModel {

    private UserDataSource userDataSource;

    private MutableLiveData<LoginFragmentState> state = new MutableLiveData<>();

    public LoginFragmentViewModel(@NonNull Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp) application;
        employersApp.getAppComponent().inject(this);
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }


    public LiveData<LoginFragmentState> getState() {
        return state;
    }

    public void sendIntent(LoginFragmentIntent intent) {
        if(intent instanceof LoginIntent) {
            processLoginIntent((LoginIntent) intent);
        }
        else {
            throw new IllegalArgumentException(
                    intent.getClass().getName() + " can not be processed");
        }
    }

    public void processLoginIntent(LoginIntent intent) {
        state.postValue(new LoadingState());
        userDataSource.login(intent.getUserLogin())
                .onComplete(new Deferrable.OnCompleteCallback<RequestResult<User>>() {
                    @Override
                    public void onComplete(RequestResult<User> result) {
                        if(result.isSuccesfull()) {
                            state.postValue(new LoggedState(result.getResult()));
                        }
                        else {

                            ServerResponse serverResponse = result.getServerResponse();

                            if(serverResponse.getStatus() == 401) {
                                state.postValue(new ErrorCredentialsState());
                            }
                            else {
                                state.postValue(new UnknownErrorState(serverResponse));
                            }

                        }
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<RequestResult<User>>() {
                    @Override
                    public void onException(Throwable t) {
                        if(t instanceof SocketTimeoutException) {
                            state.postValue(new TimeoutServerState());
                        }
                        else {
                            state.postValue(new ExceptionState(t));
                        }
                    }
                });
    }
}
