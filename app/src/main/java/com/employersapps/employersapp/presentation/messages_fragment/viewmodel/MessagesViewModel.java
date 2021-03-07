package com.employersapps.employersapp.presentation.messages_fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.MessagesDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.LastMessageChat;
import com.employersapps.core.domain.Message;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IntentIsNotSupportedException;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.messages_fragment.intent.MessagesFragmentIntent;
import com.employersapps.employersapp.presentation.messages_fragment.intent.RefreshIntent;
import com.employersapps.employersapp.presentation.messages_fragment.state.ListState;
import com.employersapps.employersapp.presentation.messages_fragment.state.MessagesFragmentState;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class MessagesViewModel extends AndroidViewModel {

    private MessagesDataSource messagesDataSource;
    private UserDataSource userDataSource;

    private MutableLiveData<MessagesFragmentState> state = new MutableLiveData<>();

    public MessagesViewModel(@NonNull Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp) application;
        employersApp.getAppComponent().inject(this);
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @Inject
    public void setMessagesDataSource(MessagesDataSource messagesDataSource) {
        this.messagesDataSource = messagesDataSource;
    }

    public void sendIntent(MessagesFragmentIntent intent) {
        if(intent instanceof RefreshIntent) {
            processRefreshIntent();
        }
        else {
            throw new IntentIsNotSupportedException(intent.getClass().getName());
        }
    }

    public boolean isUserAdmin() {
        return userDataSource.getCurrentUser()
                .getRoleId() == 1;
    }

    public long getCurrentUserId() {
        return userDataSource.getCurrentUser().getId();
    }

    private void processRefreshIntent() {
        long currentUserId = userDataSource.getCurrentUser()
                .getId();
        messagesDataSource.getChats(currentUserId).onComplete(new Deferrable.OnCompleteCallback<List<LastMessageChat>>() {
            @Override
            public void onComplete(List<LastMessageChat> result) {
                state.postValue(new ListState.Success(result
                        .stream()
                        .sorted(new Comparator<LastMessageChat>() {
                            @Override
                            public int compare(LastMessageChat o1, LastMessageChat o2) {
                                return o2.getTimestamp().compareTo(o1.getTimestamp());
                            }
                        })
                        .collect(Collectors.toList())));
            }
        }).onException(new Deferrable.OnExceptionCallback<List<LastMessageChat>>() {
                    @Override
                    public void onException(Throwable t) {
                        state.postValue(new ListState.Error(t));
                    }
                });
    }

    public LiveData<MessagesFragmentState> getState() {
        return state;
    }
}
