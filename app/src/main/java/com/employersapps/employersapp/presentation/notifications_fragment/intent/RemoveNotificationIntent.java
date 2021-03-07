package com.employersapps.employersapp.presentation.notifications_fragment.intent;

import com.employersapps.core.domain.UserNotification;

public class RemoveNotificationIntent implements NotificationsFragmentIntent {
    private final UserNotification userNotification;

    public RemoveNotificationIntent(UserNotification userNotification) {
        this.userNotification = userNotification;
    }

    public UserNotification getUserNotification() {
        return userNotification;
    }
}
