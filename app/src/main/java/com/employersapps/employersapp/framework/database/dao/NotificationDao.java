package com.employersapps.employersapp.framework.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.employersapps.employersapp.framework.database.entities.NotificationDatabase;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface NotificationDao {

    @Insert
    Single<Long> insert(NotificationDatabase notification);

    @Update
    Completable update(NotificationDatabase notification);

    @Delete
    Completable delete(NotificationDatabase notification);

    @Query("Select * from notifications order by fireDateTime desc")
    Flowable<List<NotificationDatabase>> getAllFlowable();

    @Query("Select * from notifications where id = :id")
    Single<NotificationDatabase> getById(long id);
}
