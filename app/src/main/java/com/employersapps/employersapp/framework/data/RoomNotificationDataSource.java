package com.employersapps.employersapp.framework.data;

import android.app.NotificationManager;

import com.employersapps.core.data.NotificationDataSource;
import com.employersapps.core.domain.UserNotification;
import com.employersapps.core.utils.NotificationHelper;
import com.employersapps.employersapp.framework.database.AppDatabase;
import com.employersapps.employersapp.framework.mappers.NotificationMapper;
import com.employersapps.employersapp.presentation.date_time_picker.ui.DateTimePicker;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;


@Singleton
public class RoomNotificationDataSource implements NotificationDataSource {

    private final NotificationMapper notificationMapper;
    private final AppDatabase appDatabase;
    private final NotificationHelper notificationManager;

    @Inject
    public RoomNotificationDataSource(NotificationMapper notificationMapper,
                                      AppDatabase appDatabase,
                                      NotificationHelper notificationManager) {
        this.notificationMapper = notificationMapper;
        this.appDatabase = appDatabase;
        this.notificationManager = notificationManager;
    }


    @Override
    public Flowable<List<UserNotification>> getNotificationsFlowable() {
       return appDatabase.notificationDao()
                .getAllFlowable()
                .map(notifications -> notifications.stream()
                        .map(notificationMapper::toDomain)
                        .collect(Collectors.toList()));
    }

    @Override
    public Single<Long> addNotification(UserNotification userNotification) {

        return appDatabase.notificationDao()
                .insert(notificationMapper
                        .toDatabase(userNotification))
                .doOnSuccess(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        UserNotification addedNotification = new UserNotification(
                                aLong,
                                userNotification.getText(),
                                userNotification.getFireDateTime(),
                                userNotification.getIsFired()
                        );
                        notificationManager.registerOrUpdateNotification(addedNotification);
                    }
                });
    }

    @Override
    public Completable editNotification(UserNotification userNotification) {
        return appDatabase.notificationDao()
                .update(notificationMapper
                    .toDatabase(userNotification))
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Throwable {
                        notificationManager.registerOrUpdateNotification(userNotification);
                    }
                });
    }

    @Override
    public Completable removeNotification(UserNotification userNotification) {
        return appDatabase.notificationDao()
                .delete(notificationMapper
                    .toDatabase(userNotification))
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Throwable {
                        notificationManager.cancelNotification(userNotification);
                    }
                });
    }
}
