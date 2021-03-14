package com.employersapps.employersapp.framework.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.employersapps.employersapp.framework.database.dao.MarkedMessageDao;
import com.employersapps.employersapp.framework.database.dao.NotificationDao;
import com.employersapps.employersapp.framework.database.entities.MarkedMessageEntity;
import com.employersapps.employersapp.framework.database.entities.NotificationDatabase;

@Database(
        entities = {NotificationDatabase.class, MarkedMessageEntity.class},
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public synchronized static AppDatabase getInstance(Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(
                        context,
                        AppDatabase.class,
                        "employersAppDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }


    public abstract NotificationDao notificationDao();

    public abstract MarkedMessageDao markedMessageDao();
}
