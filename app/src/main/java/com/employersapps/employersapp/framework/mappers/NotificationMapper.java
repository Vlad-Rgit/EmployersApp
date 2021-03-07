package com.employersapps.employersapp.framework.mappers;

import com.employersapps.core.domain.UserNotification;
import com.employersapps.employersapp.framework.database.entities.NotificationDatabase;

import java.time.Instant;
import java.time.ZoneId;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class NotificationMapper {

    @Inject
    public NotificationMapper() {

    }

    public NotificationDatabase toDatabase(UserNotification notification) {
        return new NotificationDatabase(
                notification.getId(),
                notification.getText(),
                notification.getFireDateTime()
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond(),
                notification.getIsFired()
        );
    }

    public UserNotification toDomain(NotificationDatabase notification) {
        return new UserNotification(
                notification.id,
                notification.text,
                Instant.ofEpochSecond(notification.fireDateTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime(),
                notification.isFired
        );
    }

}
