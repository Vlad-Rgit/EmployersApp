package com.employersapps.employersapp.presentation.notification_details_fragment.intent;

import com.employersapps.core.domain.AdminNotification;

public class AddAdminNotificationIntent implements NotificationDetailsIntent {

    private final AdminNotification adminNotification;

    public AddAdminNotificationIntent(AdminNotification adminNotification) {
        this.adminNotification = adminNotification;
    }

    public AdminNotification getAdminNotification() {
        return adminNotification;
    }
}
