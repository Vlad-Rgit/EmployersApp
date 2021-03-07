package com.employersapps.employersapp.framework.broadcast_receviers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.employersapps.core.data.NotificationDataSource;
import com.employersapps.core.domain.User;
import com.employersapps.core.domain.UserNotification;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.di.components.AppComponent;
import com.employersapps.employersapp.di.components.DaggerAppComponent;
import com.employersapps.employersapp.framework.database.AppDatabase;
import com.employersapps.employersapp.framework.database.dao.NotificationDao;
import com.employersapps.employersapp.framework.database.entities.NotificationDatabase;
import com.employersapps.employersapp.framework.domain.ParcelableUserNotification;
import com.employersapps.employersapp.framework.mappers.NotificationMapper;


import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class NotificationReceiver extends BroadcastReceiver {

    public static final String KEY_NOTIFICATION_ID =
            "com.employersapps.employersapp.framework.broadcast_receviers.NotificationId";

    private Context context;
    private NotificationDao notificationDao;

    @Override
    public void onReceive(Context context, Intent intent) {


        notificationDao = AppDatabase.getInstance(context).notificationDao();

        long notificationId = intent.getLongExtra(KEY_NOTIFICATION_ID, -1);

        if(notificationId == -1) {
            return;
        }

        notificationDao.getById(notificationId)
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<NotificationDatabase>() {
                    @Override
                    public void onSuccess(@NonNull NotificationDatabase notificationDatabase) {
                        fireNotification(context, notificationDatabase);
                        dispose();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("Error", e.getLocalizedMessage(), e);
                        dispose();
                    }
                });

    }

    private void fireNotification(Context context, NotificationDatabase notification) {

        String channelId = context.getString(R.string.employers_app_channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(notification.text)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        notificationManager.notify((int)notification.id, builder.build());

        notification.isFired = true;

        notificationDao.update(notification)
            .subscribeOn(Schedulers.io())
            .subscribe();
    }
}
