package com.employersapps.core.utils;


import com.employersapps.core.domain.UserNotification;

public interface NotificationHelper {
    void registerOrUpdateNotification(UserNotification userNotification);
    void cancelNotification(UserNotification userNotification);
}
