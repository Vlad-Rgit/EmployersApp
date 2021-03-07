package com.employersapps.employersapp.presentation.notifications_fragment.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.NotificationDataSource;
import com.employersapps.core.domain.UserNotification;
import com.employersapps.core.utils.IntentIsNotSupportedException;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.notifications_fragment.intent.NotificationsFragmentIntent;
import com.employersapps.employersapp.presentation.notifications_fragment.intent.RemoveNotificationIntent;
import com.employersapps.employersapp.presentation.notifications_fragment.state.ListState;
import com.employersapps.employersapp.presentation.notifications_fragment.state.NotificationsFragmentState;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;

public class NotificationsViewModel extends AndroidViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private NotificationDataSource notificationDataSource;

    private final MutableLiveData<NotificationsFragmentState> state
        = new MutableLiveData<>();

    public NotificationsViewModel(Application application) {

        super(application);
        EmployersApp app = (EmployersApp)application;
        app.getAppComponent().inject(this);

        compositeDisposable.add(notificationDataSource.getNotificationsFlowable()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSubscriber<List<UserNotification>>() {
                    @Override
                    public void onNext(List<UserNotification> userNotifications) {
                        state.postValue(new ListState.Success(userNotifications));
                    }

                    @Override
                    public void onError(Throwable t) {
                        state.postValue(new ListState.Error(t));
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    @Inject
    public void setNotificationDataSource(NotificationDataSource notificationDataSource) {
        this.notificationDataSource = notificationDataSource;
    }

    public LiveData<NotificationsFragmentState> getState() {
        return state;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void sendIntent(NotificationsFragmentIntent intent) {
        if(intent instanceof RemoveNotificationIntent) {
            removeNotification((RemoveNotificationIntent)intent);
        }
        else {
            throw new IntentIsNotSupportedException(intent.getClass().getName());
        }
    }

    public void removeNotification(RemoveNotificationIntent intent) {
        notificationDataSource.removeNotification(intent.getUserNotification())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
