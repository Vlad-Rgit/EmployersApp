package com.employersapps.employersapp.framework.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.employersapps.core.data.EmployerChangesDataSource;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.WebSocketInit;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.di.components.AppComponent;
import com.google.gson.Gson;
import com.rabtman.wsmanager.WsManager;
import com.rabtman.wsmanager.listener.WsStatusListener;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketEmployerChangedDataSource extends WebSocketListener implements EmployerChangesDataSource {

    private final AppComponent appComponent;

    private WebSocket webSocket;
    private final Gson gson;
    private final Context context;
    private long employerId;
    private final Subject<Employer> employerSubject = BehaviorSubject.create();


    @Inject
    public WebSocketEmployerChangedDataSource(Application app, Gson gson, Context context) {
        EmployersApp employersApp = (EmployersApp) app;
        appComponent = employersApp.getAppComponent();
        this.gson = gson;
        this.context = context;
    }

    @Override
    public void init(Employer employer) {
        init(employer.getId());
    }

    @Override
    public void init(long employerId) {
        this.employerId = employerId;
        connect();
    }

    private void connect() {

        OkHttpClient client = appComponent.getOkHttpClient();

        client.connectionPool().evictAll();

        Request request = new Request.Builder()
                .url(Config.WEB_SOCKET_ADDRESS + "/user")
                .build();

        webSocket = client.newWebSocket(request,this);

        WebSocketInit init = new WebSocketInit(employerId);

        String json = gson.toJson(init);

        webSocket.send(json);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Employer changedEmployer = gson.fromJson(
                text,
                Employer.class
        );

        employerSubject.onNext(changedEmployer);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        close();
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connect();
            }
        });
    }

    @Override
    public Subject<Employer> getChanges() {
        return employerSubject;
    }

    @Override
    public void close() {
        webSocket.close(1000, null);
        Log.i("MySocket", "Socket closed");
    }
}
