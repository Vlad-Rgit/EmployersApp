package com.employersapps.employersapp.presentation.chat_fragment.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.ChatDataSource;
import com.employersapps.core.data.EmployerChangesDataSource;
import com.employersapps.core.data.MessagesDataSource;
import com.employersapps.core.data.RealtimeMessagesDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.Message;
import com.employersapps.core.domain.MessageAttachment;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IntentIsNotSupportedException;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.chat_fragment.intent.AddAttachmentIntent;
import com.employersapps.employersapp.presentation.chat_fragment.intent.AddMessageIntent;
import com.employersapps.employersapp.presentation.chat_fragment.intent.ChatFragmentIntent;
import com.employersapps.employersapp.presentation.chat_fragment.intent.DeleteChatIntent;
import com.employersapps.employersapp.presentation.chat_fragment.intent.RemoveAttachmentIntent;
import com.employersapps.employersapp.presentation.chat_fragment.state.ChatFragmentState;
import com.employersapps.employersapp.presentation.chat_fragment.state.ChatState;
import com.employersapps.employersapp.presentation.chat_fragment.state.DeleteChatState;
import com.employersapps.employersapp.presentation.chat_fragment.state.ErrorState;
import com.employersapps.employersapp.presentation.chat_fragment.state.ListState;
import com.employersapps.employersapp.presentation.chat_fragment.state.MessageAttachmentsState;
import com.employersapps.employersapp.presentation.chat_fragment.state.ReceiverState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChatViewModel extends AndroidViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    private EmployerChangesDataSource employerChangesDataSource;
    private UserDataSource userDataSource;
    private RealtimeMessagesDataSource realtimeMessagesDataSource;
    private MessagesDataSource messagesDataSource;
    private ChatDataSource chatDataSource;
    private long currentUserId;
    private long endpointUserId;
    private Chat chat;


    private List<Message> lastList;

    private List<MessageAttachment> messageAttachments = Collections.emptyList();

    private final MutableLiveData<ChatFragmentState> chatState = new MutableLiveData<>();
    private final MutableLiveData<ChatFragmentState> state = new MutableLiveData<>();
    private final MutableLiveData<ReceiverState> receiverState = new MutableLiveData<>();

    public ChatViewModel(@NonNull Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp)application;
        employersApp.getAppComponent().inject(this);
    }

    private void initChat(Chat chat) {

        this.chat = chat;
        currentUserId = userDataSource.getCurrentUser()
                .getId();

        if(chat.isPrivate()) {

            if(chat.getUsers().get(0).getId() == currentUserId) {
                endpointUserId = chat.getUsers().get(1)
                        .getId();
            }
            else {
                endpointUserId = chat.getUsers().get(0)
                        .getId();
            }

            employerChangesDataSource.init(endpointUserId);

            Disposable disposableEmployerChanges = employerChangesDataSource.getChanges()
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<Employer>() {
                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Employer employer) {
                            receiverState.postValue(new ReceiverState(employer));
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

            compositeDisposable.add(disposableEmployerChanges);
        }
        else {
            chatState.postValue(new ChatState(chat));
        }

        realtimeMessagesDataSource.init(chat.getId());

        Disposable disposableMessages = realtimeMessagesDataSource.getMessages()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Message>>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Message> messages) {
                        if(state.getValue() instanceof ListState) {
                            ListState currentState = (ListState) state.getValue();
                            postState(new ListState(messages, currentState.getReceiver()));
                        }
                        else {
                            postState(new ListState(messages, null));
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        compositeDisposable.add(disposableMessages);
    }

    public LiveData<ChatFragmentState> getChatState() {
        return chatState;
    }

    public void refreshChatInfo() {
        if(chat != null) {
            chatDataSource.getChatDetails(chat.getId())
                    .onComplete(new Deferrable.OnCompleteCallback<Chat>() {
                        @Override
                        public void onComplete(Chat result) {
                            chat = result;
                            chatState.postValue(new ChatState(result));
                        }
                    });
        }
    }

    public void init(long chatId) {

        chatDataSource.getChatDetails(chatId)
                .onComplete(new Deferrable.OnCompleteCallback<Chat>() {
                    @Override
                    public void onComplete(Chat result) {
                        initChat(result);
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<Chat>() {
                    @Override
                    public void onException(Throwable t) {
                        state.postValue(new ErrorState(t));
                    }
                });
    }

    public boolean isUserAdmin() {
        return userDataSource.getCurrentUser()
                .getRoleId() == 1;
    }

    private synchronized void postState(ChatFragmentState chatFragmentState) {
        if(chatFragmentState instanceof ListState) {
            lastList = ((ListState) chatFragmentState).getMessages();
        }

        state.postValue(chatFragmentState);
    }

    public int attachmentsCount() {
        return messageAttachments.size();
    }

    public LiveData<ReceiverState> getReceiverState() {
        return receiverState;
    }

    private void processAddAttachmentIntent(AddAttachmentIntent intent) {
        List<MessageAttachment> newState = new ArrayList<>(messageAttachments.size() + 1);
        newState.addAll(messageAttachments);
        newState.add(intent.getMessageAttachment());
        messageAttachments = Collections.unmodifiableList(newState);
        postState(new MessageAttachmentsState(messageAttachments));
    }

    private void processRemoveAttachmentIntent(RemoveAttachmentIntent intent) {
        List<MessageAttachment> newState = new ArrayList<>(messageAttachments.size() - 1);
        MessageAttachment toDelete = intent.getMessageAttachment();
        for(int i = 0; i < messageAttachments.size(); i++) {
            if(!messageAttachments.get(i).getUriString().equals(toDelete.getUriString())) {
                newState.add(messageAttachments.get(i));
            }
        }
        messageAttachments = Collections.unmodifiableList(newState);
        postState(new MessageAttachmentsState(newState));
    }


    @Inject
    public void setChatDataSource(ChatDataSource chatDataSource) {
        this.chatDataSource = chatDataSource;
    }

    @Inject
    public void setEmployerChangesDataSource(EmployerChangesDataSource employerChangesDataSource) {
        this.employerChangesDataSource = employerChangesDataSource;
    }

    @Inject
    public void setMessagesDataSource(MessagesDataSource messagesDataSource) {
        this.messagesDataSource = messagesDataSource;
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @Inject
    public void setRealtimeMessagesDataSource(RealtimeMessagesDataSource realtimeMessagesDataSource) {
        this.realtimeMessagesDataSource = realtimeMessagesDataSource;
    }

    public LiveData<ChatFragmentState> getState() {
        return state;
    }

    public long getCurrentUserId() {
        return userDataSource.getCurrentUser().getId();
    }

    public void sendIntent(ChatFragmentIntent intent) {
        if(intent instanceof AddMessageIntent) {
            processAddMessageIntent((AddMessageIntent)intent);
        }
        else if(intent instanceof AddAttachmentIntent) {
            processAddAttachmentIntent((AddAttachmentIntent)intent);
        }
        else if(intent instanceof RemoveAttachmentIntent) {
            processRemoveAttachmentIntent((RemoveAttachmentIntent)intent);
        }
        else if(intent instanceof DeleteChatIntent) {
            processDeleteChatIntent((DeleteChatIntent) intent);
        }
        else {
            throw new IntentIsNotSupportedException(intent.getClass().getName());
        }
    }

    private void processDeleteChatIntent(DeleteChatIntent intent) {
        chatDataSource.deleteChat(chat)
                .onComplete(new Deferrable.OnCompleteCallback<ServerResponse>() {
                    @Override
                    public void onComplete(ServerResponse result) {
                        synchronized (state) {
                            state.postValue(new DeleteChatState.Success(result));
                        }
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<ServerResponse>() {
                    @Override
                    public void onException(Throwable t) {
                        synchronized (state) {
                            state.postValue(new DeleteChatState.Error(t));
                        }
                    }
                });
    }

    private void processAddMessageIntent(AddMessageIntent intent) {

        Message message = new Message (
                currentUserId,
                chat.getId(),
                intent.getText(),
                messageAttachments
        );

        message.setSending(true);

        if(lastList != null) {
            List<Message> currentState = lastList;
            List<Message> newState = new ArrayList<>(currentState.size() + 1);
            newState.addAll(currentState);
            newState.add(message);
            postState(new ListState(Collections.unmodifiableList(newState), null));
        }
        else {
            List<Message> newState = new ArrayList<>(1);
            newState.add(message);
            postState(new ListState(Collections.unmodifiableList(newState), null));
        }


        messagesDataSource.addMessage(message, messageAttachments)
                .onComplete(new Deferrable.OnCompleteCallback<ServerResponse>() {
                    @Override
                    public void onComplete(ServerResponse result) {
                        Log.i("AddMessage", "Message added");
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<ServerResponse>() {
                    @Override
                    public void onException(Throwable t) {
                        Log.e("AddMessage", t.getMessage(), t);
                    }
                });

        messageAttachments = Collections.emptyList();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        try {
            employerChangesDataSource.close();
            realtimeMessagesDataSource.close();
            compositeDisposable.clear();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
