package com.employersapps.employersapp.presentation.notification_details_fragment.intent;

import com.employersapps.core.domain.UserNotification;

public class EditNotificationIntent implements NotificationDetailsIntent {

    private final UserNotification userNotification;

    public EditNotificationIntent(UserNotification userNotification) {
        this.userNotification = userNotification;
    }

    public UserNotification getUserNotification() {
        return userNotification;
    }
}
