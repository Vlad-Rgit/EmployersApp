package com.employersapps.employersapp.framework.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.employersapps.core.data.FmsTokenDataSource;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.presentation.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class NotificationReceiverService extends FirebaseMessagingService {

    private FmsTokenDataSource fmsTokenDataSource;

    @Override
    public void onCreate() {
        super.onCreate();
        EmployersApp app = (EmployersApp) getApplication();
        fmsTokenDataSource = app.getAppComponent().getFmsTokenDataSource();
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.i("NotificationReceiver", "New Token" + s);
        fmsTokenDataSource.updateToken(s)
                .onComplete(new Deferrable.OnCompleteCallback<ServerResponse>() {
                    @Override
                    public void onComplete(ServerResponse result) {
                        Log.i("FmsToken", "Token updated");
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<ServerResponse>() {
                    @Override
                    public void onException(Throwable t) {
                        Log.e("FmsToken", t.getMessage(), t);
                    }
                });
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("NotificationReceiver", "NotificationReceived");
    }
}
