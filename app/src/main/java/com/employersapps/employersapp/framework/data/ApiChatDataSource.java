package com.employersapps.employersapp.framework.data;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.employersapps.core.data.ChatDataSource;
import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.PostGroupChat;
import com.employersapps.core.domain.PostPrivateChat;
import com.employersapps.core.domain.PutGroupChat;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.framework.util.ContentHelper;
import com.employersapps.employersapp.framework.util.DeferrableCallback;
import com.employersapps.employersapp.framework.util.StreamHelper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ApiChatDataSource implements ChatDataSource {

    private final RetrofitChatService chatService;
    private final Gson gson;
    private final Context context;

    @Inject
    public ApiChatDataSource(RetrofitChatService chatService, Gson gson, Context context) {
        this.chatService = chatService;
        this.gson = gson;
        this.context = context;
    }

    @Override
    public Deferrable<Chat> getChatDetails(long id) {
        DeferrableCallback<Chat> callback = new DeferrableCallback<>();
        chatService.getChatDetails(id).enqueue(callback);
        return callback.getDeferrable();
    }

    @Override
    public Deferrable<Chat> addPrivateChat(PostPrivateChat postPrivateChat) {
        DeferrableCallback<Chat> callback = new DeferrableCallback<>();
        chatService.postPrivateChat(postPrivateChat).enqueue(callback);
        return callback.getDeferrable();
    }

    @Override
    public Deferrable<Chat> addGroupChat(PostGroupChat postGroupChat) {

        DeferrableCallback<Chat> callback = new DeferrableCallback<>();

        Executors.newSingleThreadExecutor().execute(() -> {

            String json = gson.toJson(postGroupChat);

            RequestBody chatBody = RequestBody.create(
                    MediaType.get("application/json"),
                    json
            );

            try {

                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.parse(postGroupChat.getLogoUri());

                MediaType mimeType;

                if(uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                    mimeType = MediaType.parse(resolver.getType(uri));
                }
                else {
                    mimeType = MediaType.parse("image/png");
                }

                RequestBody logoBody = RequestBody.create(
                        mimeType,
                        StreamHelper.readAllBytes(resolver.openInputStream(uri))
                );

                MultipartBody.Part logoPart = MultipartBody.Part.createFormData(
                        "logo",
                        ContentHelper.getFileName(context, uri),
                        logoBody
                );

                chatService.postGroupChat(
                        chatBody,
                        logoPart
                ).enqueue(callback);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return callback.getDeferrable();
    }

    @Override
    public Deferrable<Chat> updateGroupChat(PutGroupChat putGroupChat) {

        DeferrableCallback<Chat> callback = new DeferrableCallback<>();

        Executors.newSingleThreadExecutor().execute(() -> {

            String json = gson.toJson(putGroupChat);

            RequestBody chatBody = RequestBody.create(
                    MediaType.get("application/json"),
                    json
            );

            try {

                List<MultipartBody.Part> logo = new ArrayList<>(1);

                if(putGroupChat.getLogoUri() != null) {

                    ContentResolver resolver = context.getContentResolver();
                    Uri uri = Uri.parse(putGroupChat.getLogoUri());

                    MediaType mimeType;

                    if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                        mimeType = MediaType.parse(resolver.getType(uri));
                    } else {
                        mimeType = MediaType.parse("image/png");
                    }

                    RequestBody logoBody = RequestBody.create(
                            mimeType,
                            StreamHelper.readAllBytes(resolver.openInputStream(uri))
                    );

                    MultipartBody.Part logoPart = MultipartBody.Part.createFormData(
                            "logo",
                            ContentHelper.getFileName(context, uri),
                            logoBody
                    );

                    logo.add(logoPart);
                }

                chatService.putGroupChat(
                        chatBody,
                        logo
                ).enqueue(callback);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return callback.getDeferrable();
    }

    @Override
    public Deferrable<ServerResponse> deleteChat(Chat chat) {
        DeferrableCallback<ServerResponse> callback = new DeferrableCallback<>();
        chatService.deleteChat(chat.getId()).enqueue(callback);
        return callback.getDeferrable();
    }
}
