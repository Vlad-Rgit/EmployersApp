package com.employersapps.employersapp.presentation.employer_details_fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.ChatDataSource;
import com.employersapps.core.data.EmployerChangesDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.PostPrivateChat;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.employer_details_fragment.intent.CreateChatIntent;
import com.employersapps.employersapp.presentation.employer_details_fragment.intent.EmployerDetailsIntent;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.ChatCreateState;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.EmployerChangedState;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.EmployerDetailsState;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class EmployerDetailsViewModel extends AndroidViewModel {

    private Employer employer;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private EmployerChangesDataSource employerChangesDataSource;

    private MutableLiveData<EmployerDetailsState> state = new MutableLiveData<>();
    private UserDataSource userDataSource;

    private ChatDataSource chatDataSource;


    public EmployerDetailsViewModel(@NonNull Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp)application;
        employersApp.getAppComponent().inject(this);
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    public long getCurrentUserId() {
        return userDataSource.getCurrentUser().getId();
    }

    @Inject
    public void setEmployerChangesDataSource(EmployerChangesDataSource employerChangesDataSource) {
        this.employerChangesDataSource = employerChangesDataSource;
    }


    @Inject
    public void setChatDataSource(ChatDataSource chatDataSource) {
        this.chatDataSource = chatDataSource;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
        employerChangesDataSource.init(employer);

        Disposable disposable =  employerChangesDataSource
                .getChanges()
                .subscribeWith(new DisposableObserver<Employer>() {

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Employer employer) {
                        state.postValue(new EmployerChangedState.Success(employer));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        state.postValue(new EmployerChangedState.Error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        compositeDisposable.add(disposable);
    }

    public void sendIntent(EmployerDetailsIntent intent) {
        if(intent instanceof CreateChatIntent) {
            createChat();
        }
    }

    private void createChat() {
        chatDataSource.addPrivateChat(new PostPrivateChat(
                getCurrentUserId(),
                employer.getId()
        ))
                .onComplete(new Deferrable.OnCompleteCallback<Chat>() {
                    @Override
                    public void onComplete(Chat result) {
                        state.postValue(new ChatCreateState.Success(result));
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<Chat>() {
                    @Override
                    public void onException(Throwable t) {
                        state.postValue(new ChatCreateState.Error(t));
                    }
                });
    }

    public LiveData<EmployerDetailsState> getState() {
        return state;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        try {
            compositeDisposable.clear();
            employerChangesDataSource.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
