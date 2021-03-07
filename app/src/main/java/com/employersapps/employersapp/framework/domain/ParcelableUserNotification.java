package com.employersapps.employersapp.framework.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.employersapps.core.domain.User;
import com.employersapps.core.domain.UserNotification;

import java.time.Instant;
import java.time.ZoneId;

public class ParcelableUserNotification implements Parcelable {

    private final UserNotification userNotification;

    public ParcelableUserNotification(UserNotification userNotification) {
        this.userNotification = userNotification;
    }

    private ParcelableUserNotification(Parcel in) {
        userNotification = new UserNotification(
                in.readLong(),
                in.readString(),
                Instant.ofEpochSecond(in.readLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime(),
                in.readByte() == 1
        );
    }

    public UserNotification getUserNotification() {
        return userNotification;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userNotification.getId());
        dest.writeString(userNotification.getText());
        dest.writeLong(userNotification.getFireDateTime()
            .atZone(ZoneId.systemDefault())
            .toEpochSecond());
        byte isFired = (byte)(userNotification.getIsFired() ? 1 : 0);
        dest.writeByte(isFired);
    }

    public static final Parcelable.Creator<ParcelableUserNotification> CREATOR =
            new Parcelable.Creator<ParcelableUserNotification>() {

        @Override
        public ParcelableUserNotification createFromParcel(Parcel source) {
            return new ParcelableUserNotification(source);
        }

        @Override
        public ParcelableUserNotification[] newArray(int size) {
            return new ParcelableUserNotification[0];
        }
    };
}
