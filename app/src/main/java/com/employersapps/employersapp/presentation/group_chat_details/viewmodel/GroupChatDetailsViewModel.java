package com.employersapps.employersapp.presentation.group_chat_details.viewmodel;

import android.app.Application;
import android.service.autofill.UserData;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.ChatDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.PostGroupChat;
import com.employersapps.core.domain.PutGroupChat;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.group_chat_details.models.ChatData;
import com.employersapps.employersapp.presentation.group_chat_details.models.Result;
import com.employersapps.employersapp.presentation.group_chat_details.models.ResultData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GroupChatDetailsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Employer>> employersLiveData =
            new MutableLiveData<>(Collections.emptyList());

    private final MutableLiveData<Result> resultLiveData =
            new MutableLiveData<>(new ResultData.NoResult());

    private ChatDataSource chatDataSource;
    private UserDataSource userDataSource;

    public GroupChatDetailsViewModel(Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp) application;
        employersApp.getAppComponent().inject(this);
    }


    @Inject
    public void setChatDataSource(ChatDataSource chatDataSource) {
        this.chatDataSource = chatDataSource;
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    public void addEmployers(List<Employer> newEmployers) {
        List<Employer> employers = employersLiveData.getValue();
        List<Employer> newList = new ArrayList<>(
                employers.size() + newEmployers.size());
        newList.addAll(employers);

        for(Employer e : newEmployers) {
           boolean exists = false;
           for(Employer n : newList) {
               if(n.getId() == e.getId()) {
                   exists = true;
                   break;
               }
           }
           if(!exists) {
               newList.add(e);
           }
        }

        employersLiveData.setValue(newList);
    }

    public void removeEmployer(Employer employer) {

        List<Employer> employers = employersLiveData.getValue();
        List<Employer> newList = new ArrayList<>(employers.size() - 1);

        for(Employer e : employers) {
            if(e.getId() != employer.getId()) {
                newList.add(e);
            }
        }

        employersLiveData.setValue(newList);
    }


    public boolean isEmployersEmpty() {
        return employersLiveData.getValue().isEmpty();
    }

    public LiveData<Result> getResultLiveData() {
        return resultLiveData;
    }

    public LiveData<List<Employer>> getEmployersLiveData() {
        return employersLiveData;
    }

    public void updateGroupChat(ChatData chatData) {

        List<Employer> employers = employersLiveData.getValue();
        List<Long> userIds = new ArrayList<>(employers.size() + 1);

        for(Employer e : employers) {
            userIds.add(e.getId());
        }

        userIds.add(userDataSource.getCurrentUser()
            .getId());

        PutGroupChat putGroupChat = new PutGroupChat(
                chatData.getId(),
                chatData.getName(),
                chatData.getUri(),
                chatData.isAllowWriteOnlyForAdmin(),
                userIds
        );

        chatDataSource.updateGroupChat(putGroupChat)
                .onComplete(new Deferrable.OnCompleteCallback<Chat>() {
                    @Override
                    public void onComplete(Chat result) {
                        resultLiveData.postValue(new ResultData.Success(result));
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<Chat>() {
                    @Override
                    public void onException(Throwable t) {
                        resultLiveData.postValue(new ResultData.Error(t));
                    }
                });
    }

    public void postGroupChat(ChatData chatData) {

        List<Employer> employers = employersLiveData.getValue();
        List<Long> userIds = new ArrayList<>(employers.size() + 1);

        for(Employer e : employers) {
            userIds.add(e.getId());
        }

        long currentUserId = userDataSource.getCurrentUser().getId();

        userIds.add(currentUserId);

        PostGroupChat postGroupChat = new PostGroupChat(
                chatData.getName(),
                chatData.getUri(),
                chatData.isAllowWriteOnlyForAdmin(),
                userIds,
                currentUserId
        );

        chatDataSource.addGroupChat(postGroupChat)
                .onComplete(new Deferrable.OnCompleteCallback<Chat>() {
                    @Override
                    public void onComplete(Chat result) {
                        resultLiveData.postValue(new ResultData.Success(result));
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<Chat>() {
                    @Override
                    public void onException(Throwable t) {
                        resultLiveData.postValue(new ResultData.Error(t));
                    }
                });
    }

}
