package com.employersapps.employersapp.framework.data;

import android.graphics.DashPathEffect;
import android.nfc.tech.NdefFormatable;

import com.employersapps.core.data.EmployersDataSource;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IllegalRequestException;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiEmployersDataSource implements EmployersDataSource {

    private final BehaviorSubject<List<Employer>> employersSubject = BehaviorSubject.create();
    private final BehaviorSubject<Throwable> exceptionsSubject = BehaviorSubject.create();
    private final RetrofitEmployersService employersService;

    @Inject
    public ApiEmployersDataSource(RetrofitEmployersService employersService) {
        this.employersService = employersService;
    }

    @Override
    public Subject<List<Employer>> getEmployers() {
        return employersSubject;
    }

    @Override
    public Deferrable<Employer> getEmployer(long userId) {
        Deferrable<Employer> deferrable = new Deferrable<>();

        employersService.getEmployer(userId).enqueue(new Callback<Employer>() {
            @Override
            public void onResponse(Call<Employer> call, Response<Employer> response) {
                if(response.isSuccessful()) {
                    deferrable.complete(response.body());
                }
                else {
                    deferrable.completeWithException(new IllegalRequestException(
                            response.code(), response.message()
                    ));
                }
            }

            @Override
            public void onFailure(Call<Employer> call, Throwable t) {
                deferrable.completeWithException(t);
            }
        });

        return deferrable;
    }

    @Override
    public Subject<Throwable> getExceptions() {
        return exceptionsSubject;
    }


    @Override
    public void refreshEmployers() {
        employersService.getEmployers().enqueue(new Callback<List<Employer>>() {
            @Override
            public void onResponse(Call<List<Employer>> call, Response<List<Employer>> response) {
                if(response.isSuccessful()) {
                    employersSubject.onNext(response.body());
                }
                else {
                    exceptionsSubject.onNext(new IllegalRequestException(
                            response.code(),
                            response.message()
                    ));
                }
            }

            @Override
            public void onFailure(Call<List<Employer>> call, Throwable t) {
                exceptionsSubject.onNext(t);
            }
        });
    }

    @Override
    public Deferrable<Void> updateStatus(long user, int statusId) {

        Deferrable<Void> deferrable = new Deferrable<>();

        employersService.updateStatus(user, statusId).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()) {
                    deferrable.complete(null);
                }
                else {
                    deferrable.completeWithException(new IllegalRequestException(
                            response.code(), response.message()
                    ));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                deferrable.completeWithException(t);
            }
        });

        return deferrable;
    }
}
