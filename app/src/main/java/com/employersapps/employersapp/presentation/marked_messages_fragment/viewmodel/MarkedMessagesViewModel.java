package com.employersapps.employersapp.presentation.marked_messages_fragment.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.employersapps.core.data.EmployersDataSource;
import com.employersapps.core.data.MarkedMessageDataSource;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.MarkedMessage;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.framework.domain.ParcelableEmployer;
import com.employersapps.employersapp.presentation.date_time_picker.ui.DateTimePicker;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;

public class MarkedMessagesViewModel extends AndroidViewModel {

    public class Filter {

        private String query;
        private LocalDate date;
        private Long senderId;

        public Filter(String query, LocalDate date, Long senderId) {
            this.query = query;
            this.date = date;
            this.senderId = senderId;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public Long getSenderId() {
            return senderId;
        }

        public void setSenderId(long senderId) {
            this.senderId = senderId;
        }
    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MarkedMessageDataSource dataSource;

    private MutableLiveData<List<MarkedMessage>> databaseLive
            = new MutableLiveData<>();

    private MutableLiveData<Filter> filterLive
            = new MutableLiveData<>();

    private MediatorLiveData<List<MarkedMessage>> liveData
            = new MediatorLiveData<>();

    private EmployersDataSource employersDataSource;

    private MutableLiveData<List<Employer>> employersLiveData
            = new MutableLiveData<>();

    public MarkedMessagesViewModel(Application application) {

        super(application);

        EmployersApp app = (EmployersApp) application;

        app.getAppComponent().inject(this);

        Disposable disposable = dataSource.getMessages()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSubscriber<List<MarkedMessage>>() {
                    @Override
                    public void onNext(List<MarkedMessage> markedMessages) {
                        databaseLive.postValue(markedMessages);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        compositeDisposable.add(disposable);

        liveData.addSource(databaseLive, new Observer<List<MarkedMessage>>() {
            @Override
            public void onChanged(List<MarkedMessage> markedMessages) {

                Filter filter = filterLive.getValue();

                updateList(markedMessages, filter);
            }
        });

        liveData.addSource(filterLive, new Observer<Filter>() {
            @Override
            public void onChanged(Filter filter) {
                List<MarkedMessage> markedMessages
                        = databaseLive.getValue();

                updateList(markedMessages, filter);
            }
        });

        Disposable employersDisposable = employersDataSource.getEmployers()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Employer>>() {
                    @Override
                    public void onNext(@NonNull List<Employer> employers) {
                        employersLiveData.postValue(employers);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        compositeDisposable.add(employersDisposable);

    }

    private synchronized void updateList(List<MarkedMessage> markedMessages,
                                         Filter filter) {

        if(markedMessages == null) {
            liveData.postValue(Collections.EMPTY_LIST);
            return;
        }

        if(filter == null) {
            liveData.postValue(markedMessages);
            return;
        }

        List<MarkedMessage> filtered = new ArrayList<>();

        String[] keywords = null;

        if(filter.getQuery() != null) {
            keywords = filter.getQuery()
                    .split(" ");
        }

        for (MarkedMessage m : markedMessages) {

            boolean isAdded = false;

            if(keywords != null) {
                for (String k : keywords) {
                    if(m.getText().contains(k) ||
                        m.getChatName().contains(k) ||
                        m.getSender().contains(k)) {
                        filtered.add(m);
                        isAdded = true;
                        break;
                    }
                }
            }

            LocalDate date = filter.getDate();

            if(date != null && !isAdded) {
                if(m.getTimestamp().toLocalDate()
                    .isEqual(date)) {
                    filtered.add(m);
                    continue;
                }
            }
        }

        liveData.postValue(filtered);

    }

    public void updateFilter(LocalDate date, String query) {
        filterLive.postValue(new Filter(
                query,
                date,
                null
        ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    @Inject
    public void setEmployersDataSource(EmployersDataSource employersDataSource) {
        this.employersDataSource = employersDataSource;
    }

    @Inject
    public void setDataSource(MarkedMessageDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public LiveData<List<MarkedMessage>> getLiveData() {
        return liveData;
    }

    public LiveData<List<Employer>> getEmployersLiveData() {
        return employersLiveData;
    }
}
