package com.employersapps.core.data;

import com.employersapps.core.domain.UserNotification;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface NotificationDataSource {
    Flowable<List<UserNotification>> getNotificationsFlowable();
    Single<Long> addNotification(UserNotification userNotification);
    Completable editNotification(UserNotification userNotification);
    Completable removeNotification(UserNotification userNotification);
}
