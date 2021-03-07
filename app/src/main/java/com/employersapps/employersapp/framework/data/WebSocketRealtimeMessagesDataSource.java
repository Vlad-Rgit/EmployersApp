package com.employersapps.employersapp.framework.data;

import android.content.Context;

import com.employersapps.core.data.RealtimeMessagesDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.Message;
import com.employersapps.core.domain.MessagesInit;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.config.Config;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabtman.wsmanager.WsManager;
import com.rabtman.wsmanager.listener.WsStatusListener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;



public class WebSocketRealtimeMessagesDataSource extends WebSocketListener implements RealtimeMessagesDataSource {

    private final Subject<List<Message>> messagesSubject = BehaviorSubject.create();

    private WebSocket wsManager;
    private final Context context;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private boolean connected;
    private List<Message> messages;
    private long chatId;
    private final UserDataSource userDataSource;

    @Inject
    public WebSocketRealtimeMessagesDataSource(Context context,
                                               OkHttpClient httpClient,
                                               Gson gson,
                                               UserDataSource userDataSource) {
        this.context = context;
        this.httpClient = httpClient;
        this.gson = gson;
        this.userDataSource = userDataSource;
    }

    @Override
    public void init(long chatId) {
        this.chatId = chatId;
        connect();
    }

    private void connect() {

        messages = null;
        httpClient.connectionPool().evictAll();

        Request request = new Request.Builder()
                .url(Config.WEB_SOCKET_ADDRESS + "/chat")
                .build();

        wsManager = httpClient.newWebSocket(request, this);


        long userId = userDataSource.getCurrentUser().getId();
        MessagesInit init = new MessagesInit("init", chatId, userId);
        String json = gson.toJson(init);

        wsManager.send(json);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        connected = true;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Type type = new TypeToken<List<Message>>(){}.getType();
        if(messages == null) {
            messages = Collections.unmodifiableList(gson.fromJson(text, type));
        }
        else {
            List<Message> newMessages = gson.fromJson(text, type);
            List<Message> temp = new ArrayList<>(messages.size() + newMessages.size());
            temp.addAll(messages);
            temp.addAll(newMessages);
            messages = Collections.unmodifiableList(temp);
        }
        messagesSubject.onNext(messages.stream().sorted(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        }).collect(Collectors.toList()));
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        wsManager.close(1000, null);
        connected = false;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {

        super.onFailure(webSocket, t, response);
        close();


        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    connect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Subject<List<Message>> getMessages() {
        return messagesSubject;
    }

    @Override
    public void close() {
        wsManager.close(1000, null);
        connected = false;
    }
}
