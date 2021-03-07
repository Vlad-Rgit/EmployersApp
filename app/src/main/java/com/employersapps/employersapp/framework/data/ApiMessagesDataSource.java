package com.employersapps.employersapp.framework.data;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.DashPathEffect;
import android.net.Uri;

import com.employersapps.core.data.MessagesDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.LastMessageChat;
import com.employersapps.core.domain.Message;
import com.employersapps.core.domain.MessageAttachment;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;
import com.employersapps.employersapp.framework.util.ContentHelper;
import com.employersapps.employersapp.framework.util.DeferrableCallback;
import com.employersapps.employersapp.framework.util.StreamHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class ApiMessagesDataSource implements MessagesDataSource {

    private final RetrofitMessagesService messageService;
    private final UserDataSource userDataSource;
    private final Context context;
    private final Gson gson;

    @Inject
    public ApiMessagesDataSource(RetrofitMessagesService messageService,
                                 UserDataSource userDataSource,
                                 Context context,
                                 Gson gson) {
        this.messageService = messageService;
        this.userDataSource = userDataSource;
        this.context = context;
        this.gson = gson;
    }

    @Override
    public Deferrable<List<Message>> getMessages() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Deferrable<List<LastMessageChat>> getChats(long userId) {
        DeferrableCallback<List<LastMessageChat>> callback = new DeferrableCallback<>();
        messageService.getMessages(userId).enqueue(callback);
        return callback.getDeferrable();
    }

    @Override
    public Deferrable<ServerResponse> addMessage(Message message, List<MessageAttachment> attachments) {

        Deferrable<ServerResponse> deferrable = new Deferrable<>();

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {

                    ContentResolver resolver = context.getContentResolver();

                    RequestBody requestBody = RequestBody.create(
                            MediaType.parse("application/json"),
                            gson.toJson(message)
                    );

                    List<MultipartBody.Part> parts = new ArrayList<>(attachments.size());

                    for (MessageAttachment attachment : attachments) {
                        Uri uri = Uri.parse(attachment.getUriString());

                        RequestBody body = RequestBody.create(
                                MediaType.parse(resolver.getType(uri)),
                                StreamHelper.readAllBytes(resolver.openInputStream(uri))
                        );

                        MultipartBody.Part part = MultipartBody.Part.createFormData(
                                "files",
                                ContentHelper.getFileName(context, uri),
                                body
                        );

                        parts.add(part);
                    }


                    messageService.postMessage(requestBody, parts).enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            if (response.isSuccessful()) {
                                deferrable.complete(response.body());
                            } else {
                                deferrable.completeWithException(new IllegalRequestException(
                                        response.code(),
                                        response.message()
                                ));
                            }
                        }

                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            deferrable.completeWithException(t);
                        }
                    });

                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    deferrable.completeWithException(ex);
                }
            }
        });


        return deferrable;
    }
}
