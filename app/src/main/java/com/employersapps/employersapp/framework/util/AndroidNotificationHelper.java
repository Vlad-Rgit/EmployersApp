package com.employersapps.employersapp.framework.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.employersapps.core.domain.UserNotification;
import com.employersapps.core.utils.NotificationHelper;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.framework.broadcast_receviers.NotificationReceiver;
import com.employersapps.employersapp.framework.domain.ParcelableUserNotification;


import java.time.ZoneId;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class AndroidNotificationHelper implements NotificationHelper {


    private final Context context;

    @Inject
    public AndroidNotificationHelper(Context context) {
        this.context = context;
    }

    private PendingIntent buildPendingIntent(UserNotification userNotification) {

        Intent intent = new Intent(context, NotificationReceiver.class);

        intent.putExtra(NotificationReceiver.KEY_NOTIFICATION_ID, userNotification.getId());

        return PendingIntent.getBroadcast(
                context,
                (int)userNotification.getId(), // request code
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
    }

    public void registerOrUpdateNotification(UserNotification userNotification) {

        PendingIntent pendingIntent = buildPendingIntent(userNotification);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        long millis = userNotification.getFireDateTime()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
    }


    public void cancelNotification(UserNotification userNotification) {

        PendingIntent pendingIntent = buildPendingIntent(userNotification);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
    }

    public Notification getForegroundServiceNotification(String text) {

        String channelId = context.getString(R.string.employers_app_channel);

        return new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("Сервис")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
    }

}
