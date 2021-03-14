package com.employersapps.employersapp.framework.data;

import com.employersapps.core.data.MarkedMessageDataSource;
import com.employersapps.core.domain.MarkedMessage;
import com.employersapps.employersapp.framework.database.AppDatabase;
import com.employersapps.employersapp.framework.database.dao.MarkedMessageDao;
import com.employersapps.employersapp.framework.database.entities.MarkedMessageEntity;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class RoomMarkedMessageDataSource implements MarkedMessageDataSource {


    private final MarkedMessageDao messageDao;

    @Inject
    public RoomMarkedMessageDataSource(
            AppDatabase database
    ) {
        messageDao = database.markedMessageDao();
    }

    @Override
    public Flowable<List<MarkedMessage>> getMessages() {
        return messageDao.getAllFlowable()
                .map((entities) -> {
                   return entities.stream()
                            .map((e) -> new MarkedMessage(
                                 e.getId(),
                                 e.getSenderId(),
                                 e.getChatId(),
                                 e.getText(),
                                 e.getSender(),
                                 e.getLogoPath(),
                                    Instant.ofEpochMilli(e.getTimestamp())
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime(),
                                    e.getChatName(),
                                    e.getFirstReadTimestamp() == null ? null
                                            : Instant.ofEpochMilli(e.getFirstReadTimestamp())
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDateTime(),
                                    e.isPrivate()
                            ))
                           .collect(Collectors.toList());
                });
    }

    @Override
    public Single<Long> addMessage(MarkedMessage message) {

        MarkedMessageEntity entity = new MarkedMessageEntity(
                message.getId(),
                message.getSenderId(),
                message.getChatId(),
                message.getText(),
                message.getSender(),
                message.getLogoPath(),
                message.getTimestamp()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli(),
                message.getChatName(),
                message.getFirstReadTimestamp() == null ? null
                        : message.getFirstReadTimestamp()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                message.isPrivate()
        );

        return messageDao.insert(entity);
    }

    @Override
    public Completable removeMessage(MarkedMessage message) {

        MarkedMessageEntity entity = new MarkedMessageEntity(
                message.getId(),
                message.getSenderId(),
                message.getChatId(),
                message.getText(),
                message.getSender(),
                message.getLogoPath(),
                message.getTimestamp()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                message.getChatName(),
                message.getFirstReadTimestamp() == null ? null
                        : message.getFirstReadTimestamp()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                message.isPrivate()
        );

        return messageDao.delete(entity);
    }
}
