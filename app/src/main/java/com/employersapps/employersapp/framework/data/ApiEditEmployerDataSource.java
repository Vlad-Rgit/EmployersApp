package com.employersapps.employersapp.framework.data;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.DashPathEffect;
import android.net.Uri;

import com.employersapps.core.data.EditEmployersDataSource;
import com.employersapps.core.domain.network.PostEmployer;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.framework.util.ContentHelper;
import com.employersapps.employersapp.framework.util.StreamHelper;
import com.google.gson.Gson;

import java.util.ArrayList;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiEditEmployerDataSource implements EditEmployersDataSource {

    private final RetrofitEmployersService employersService;
    private final Gson gson;
    private final Context context;

    @Inject
    public ApiEditEmployerDataSource(RetrofitEmployersService employersService,
                                     Gson gson,
                                     Context context) {
        this.employersService = employersService;
        this.gson = gson;
        this.context = context;
    }

    @Override
    public Deferrable<ServerResponse> addEmployer(PostEmployer employer, String photoUri) {

        Deferrable<ServerResponse> deferrable = new Deferrable<>();

        try {

            String json = gson.toJson(employer);

            RequestBody body = RequestBody.create(
                    MediaType.get("application/json"),
                    json
            );

            ContentResolver resolver = context.getContentResolver();


            RequestBody photoBody;
            String fileName;

            if(photoUri == null) {
                photoBody = RequestBody.create(
                        MediaType.get("image/png"),
                        StreamHelper.readAllBytes(
                                context.getResources().openRawResource(
                                        R.raw.no_image
                                )
                        )
                );

                fileName = "no_image";
            }
            else {
                Uri uri = Uri.parse(photoUri);

                photoBody = RequestBody.create(
                        MediaType.parse(resolver.getType(uri)),
                        StreamHelper.readAllBytes(
                                resolver.openInputStream(uri)
                        )
                );

                fileName = ContentHelper.getFileName(context, uri);
            }


            MultipartBody.Part photoPart = MultipartBody.Part.createFormData(
                "photo",
                    fileName,
                    photoBody
            );

            employersService.addUser(body, photoPart).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if(response.isSuccessful()) {
                        deferrable.complete(response.body());
                    }
                    else {
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
            deferrable.completeWithException(ex);
        }

        return deferrable;
    }

    @Override
    public Deferrable<ServerResponse> editEmployer(PostEmployer employer, String photoUri) {
        Deferrable<ServerResponse> deferrable = new Deferrable<>();

        try {

            String json = gson.toJson(employer);

            RequestBody body = RequestBody.create(
                    MediaType.get("application/json"),
                    json
            );

            ContentResolver resolver = context.getContentResolver();

            ArrayList<MultipartBody.Part> photo = new ArrayList<>(1);

            RequestBody photoBody;
            String fileName;

            if(photoUri != null) {

                Uri uri = Uri.parse(photoUri);

                photoBody = RequestBody.create(
                        MediaType.parse(resolver.getType(uri)),
                        StreamHelper.readAllBytes(
                                resolver.openInputStream(uri)
                        )
                );

                fileName = ContentHelper.getFileName(context, uri);

                MultipartBody.Part photoPart = MultipartBody.Part.createFormData(
                        "photo",
                        fileName,
                        photoBody
                );
                photo.add(photoPart);
            }

            employersService.updateUser(body, photo).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if(response.isSuccessful()) {
                        deferrable.complete(response.body());
                    }
                    else {
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
            deferrable.completeWithException(ex);
        }

        return deferrable;
    }
}
