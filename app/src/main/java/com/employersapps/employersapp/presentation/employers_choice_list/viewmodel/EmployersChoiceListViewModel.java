package com.employersapps.employersapp.presentation.employers_choice_list.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.EmployersDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.Employer;
import com.employersapps.employersapp.EmployersApp;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class EmployersChoiceListViewModel extends AndroidViewModel {

    private EmployersDataSource employersDataSource;
    private UserDataSource userDataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<List<Employer>> employersLiveData = new MutableLiveData<>();

    public EmployersChoiceListViewModel(@NonNull @NotNull Application application) {
        super(application);
        EmployersApp app = (EmployersApp) application;
        app.getAppComponent().inject(this);
    }

    public void fetchEmployers() {

        long currentUserId = userDataSource.getCurrentUser()
                .getId();

        Disposable disposable = employersDataSource.getEmployers()
                .map((list) ->
                     list.stream()
                             .filter(employer -> employer.getId() != currentUserId)
                             .collect(Collectors.toList())
                )
                .subscribeWith(new DisposableObserver<List<Employer>>() {
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Employer> employers) {
                employersLiveData.postValue(employers);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


        compositeDisposable.add(disposable);

        employersDataSource.refreshEmployers();
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @Inject
    public void setEmployersDataSource(EmployersDataSource employersDataSource) {
        this.employersDataSource = employersDataSource;
    }

    public LiveData<List<Employer>> getEmployers() {
        return employersLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
