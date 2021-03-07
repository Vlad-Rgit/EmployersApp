package com.employersapps.core.data;

import com.employersapps.core.domain.Message;

import java.io.Closeable;
import java.util.List;

import io.reactivex.rxjava3.subjects.Subject;


public interface RealtimeMessagesDataSource extends Closeable {
    Subject<List<Message>> getMessages();
    void init(long chatId);
}
