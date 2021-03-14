package com.employersapps.employersapp;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.employersapps.employersapp.di.components.AppComponent;
import com.employersapps.employersapp.di.components.DaggerAppComponent;
import com.employersapps.employersapp.framework.services.LocationLoggerService;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.framework.workers.LocationLogWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class EmployersApp extends Application {

    private AppComponent appComponent;

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {

        super.onCreate();


        appComponent = DaggerAppComponent
                .builder()
                .context(getApplicationContext())
                .app(this)
                .build();

        createNotificationChannel(getApplicationContext());
        createMessageChannel(getApplicationContext());
        registerPeriodLogger();
    }

    private void registerPeriodLogger() {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                LocationLogWorker.class,
                20,
                TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "LocationLogger",
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );
    }


    private void createNotificationChannel(Context context) {
        if(Build.VERSION.SDK_INT >= 26) {
            String channelId = context.getString(R.string.employers_app_channel);
            String name = context.getString(R.string.employers_app_channel);
            String description = context.getString(R.string.employers_app_channel_desc);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createMessageChannel(Context context) {
        if(Build.VERSION.SDK_INT >= 26) {
            String channelId = context.getString(R.string.message_channel_id);
            String name = context.getString(R.string.message_channel_name);
            String desc = context.getString(R.string.message_channel_desc);
            NotificationChannel channel = new NotificationChannel(channelId,
                    name,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(desc);
            if(Build.VERSION.SDK_INT >= 29) {
                channel.setAllowBubbles(true);
            }
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
