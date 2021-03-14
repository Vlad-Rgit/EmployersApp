package com.employersapps.employersapp.framework.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.employersapps.employersapp.framework.database.entities.MarkedMessageEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MarkedMessageDao {

    @Query("Select * from marked_messages")
    Flowable<List<MarkedMessageEntity>> getAllFlowable();

    @Insert
    Single<Long> insert(MarkedMessageEntity entity);

    @Update
    Completable update(MarkedMessageEntity entity);

    @Delete
    Completable delete(MarkedMessageEntity entity);

}
