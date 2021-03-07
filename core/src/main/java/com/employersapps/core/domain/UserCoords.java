package com.employersapps.core.domain;

import java.time.LocalDateTime;

public class UserCoords {
    private final long userId;
    private final LocalDateTime timestamp;
    private final double longitude;
    private final double latitude;

    public UserCoords(long userId, LocalDateTime timestamp, double longitude, double latitude) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public UserCoords(long userId, double longitude, double latitude) {
        this(userId, LocalDateTime.now(), longitude, latitude);
    }

    public long getUserId() {
        return userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
