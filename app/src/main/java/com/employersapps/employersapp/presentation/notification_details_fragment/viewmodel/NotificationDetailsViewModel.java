package com.employersapps.employersapp.presentation.notification_details_fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.AdminNotificationsDataSource;
import com.employersapps.core.data.NotificationDataSource;
import com.employersapps.core.domain.User;
import com.employersapps.core.domain.UserNotification;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IntentIsNotSupportedException;
import com.employersapps.core.utils.NotificationHelper;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.NewsDetailsIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsDetailsState;
import com.employersapps.employersapp.presentation.notification_details_fragment.intent.AddAdminNotificationIntent;
import com.employersapps.employersapp.presentation.notification_details_fragment.intent.AddNotificationIntent;
import com.employersapps.employersapp.presentation.notification_details_fragment.intent.EditNotificationIntent;
import com.employersapps.employersapp.presentation.notification_details_fragment.intent.NotificationDetailsIntent;
import com.employersapps.employersapp.presentation.notification_details_fragment.state.NotificationDetailsState;
import com.employersapps.employersapp.presentation.notification_details_fragment.state.TransactionState;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.internal.observers.BiConsumerSingleObserver;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotificationDetailsViewModel extends AndroidViewModel {

    private NotificationDataSource notificationDataSource;
    private NotificationHelper notificationHelper;
    private AdminNotificationsDataSource adminNotificationsDataSource;

    private MutableLiveData<NotificationDetailsState> state = new MutableLiveData<>();

    public NotificationDetailsViewModel(@NonNull Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp)application;
        employersApp.getAppComponent().inject(this);
    }

    @Inject
    public void setNotificationDataSource(NotificationDataSource notificationDataSource) {
        this.notificationDataSource = notificationDataSource;
    }

    @Inject
    public void setNotificationHelper(NotificationHelper notificationHelper) {
        this.notificationHelper = notificationHelper;
    }

    @Inject
    public void setAdminNotificationsDataSource(AdminNotificationsDataSource adminNotificationsDataSource) {
        this.adminNotificationsDataSource = adminNotificationsDataSource;
    }

    public void sendIntent(NotificationDetailsIntent intent) {
        if(intent instanceof AddNotificationIntent) {
            processAddIntent((AddNotificationIntent)intent);
        }
        else if(intent instanceof EditNotificationIntent) {
            processEditIntent((EditNotificationIntent)intent);
        }
        else if(intent instanceof AddAdminNotificationIntent) {
            proccessAddAdminNotification((AddAdminNotificationIntent)intent);
        }
        else {
            throw new IntentIsNotSupportedException(intent.getClass().getName());
        }
    }

    private void proccessAddAdminNotification(AddAdminNotificationIntent intent) {
        adminNotificationsDataSource.postNotification(intent.getAdminNotification())
                .onComplete(new Deferrable.OnCompleteCallback<ServerResponse>() {
                    @Override
                    public void onComplete(ServerResponse result) {
                        state.postValue(new TransactionState.TransactionSuccessState(null));
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<ServerResponse>() {
                    @Override
                    public void onException(Throwable t) {
                        state.postValue(new TransactionState.TransactionErrorState(t));
                    }
                });
    }

    public LiveData<NotificationDetailsState> getState() {
        return state;
    }

    private void processAddIntent(AddNotificationIntent intent) {

        UserNotification userNotification = intent.getUserNotification();

        notificationDataSource.addNotification(userNotification)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        new DisposableSingleObserver<Long>() {

                            @Override
                            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Long id) {

                                UserNotification addedNotification = new UserNotification(
                                        id,
                                        userNotification.getText(),
                                        userNotification.getFireDateTime(),
                                        userNotification.getIsFired()
                                );

                                state.postValue(new TransactionState
                                        .TransactionSuccessState(addedNotification));

                                dispose();
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                state.postValue(new TransactionState
                                        .TransactionErrorState(e));

                                dispose();
                            }
                        }
                );
    }

    private void processEditIntent(EditNotificationIntent intent) {
        notificationDataSource.editNotification(intent.getUserNotification())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                        state.postValue(new TransactionState.TransactionSuccessState(
                                intent.getUserNotification()
                        ));
                        dispose();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        state.postValue(new TransactionState.TransactionErrorState(e));
                        dispose();
                    }
                });
    }
}
