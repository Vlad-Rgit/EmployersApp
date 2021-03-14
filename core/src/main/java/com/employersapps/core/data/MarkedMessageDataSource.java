package com.employersapps.core.data;

import com.employersapps.core.domain.MarkedMessage;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface MarkedMessageDataSource {

    Flowable<List<MarkedMessage>> getMessages();

    Single<Long> addMessage(MarkedMessage message);

    Completable removeMessage(MarkedMessage message);

}
