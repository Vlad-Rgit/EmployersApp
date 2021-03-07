package com.employersapps.employersapp.framework.database.entities;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class NotificationDatabase {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String text;
    public long fireDateTime;
    public boolean isFired;

    public NotificationDatabase(long id,
                                String text,
                                long fireDateTime,
                                boolean isFired) {
        this.id = id;
        this.text = text;
        this.fireDateTime = fireDateTime;
        this.isFired = isFired;
    }
}
