package com.employersapps.employersapp.presentation.news_fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.NewsDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.News;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IntentIsNotSupportedException;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.news_fragment.intents.AddNewsIntent;
import com.employersapps.employersapp.presentation.news_fragment.intents.DeleteNewsIntent;
import com.employersapps.employersapp.presentation.news_fragment.intents.NewsFragmentIntent;
import com.employersapps.employersapp.presentation.news_fragment.intents.RefreshNewsIntent;
import com.employersapps.employersapp.presentation.news_fragment.states.ExceptionState;
import com.employersapps.employersapp.presentation.news_fragment.states.ListNewsState;
import com.employersapps.employersapp.presentation.news_fragment.states.NewsFragmentState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;

public class NewsFragmentViewModel extends AndroidViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private NewsDataSource newsDataSource;
    private UserDataSource userDataSource;

    private final MutableLiveData<NewsFragmentState> state = new MutableLiveData<>();

    public NewsFragmentViewModel(@NonNull Application application) {
        super(application);
        EmployersApp app = (EmployersApp)application;
        app.getAppComponent().inject(this);
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    public boolean isAdmin() {
        return userDataSource.getCurrentUser()
                .getRoleId() == 1;
    }

    @Inject
    public void setNewsDataSource(NewsDataSource newsDataSource) {
        this.newsDataSource = newsDataSource;

        compositeDisposable.add(this.newsDataSource.getNewsSubject().subscribe(new Consumer<List<News>>() {
            @Override
            public void accept(List<News> news) throws Throwable {
                state.postValue(new ListNewsState(news));
            }
        }));

        compositeDisposable.add(this.newsDataSource.getExceptionsSubject().subscribe(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                state.postValue(new ExceptionState(throwable));
            }
        }));
    }

    public LiveData<NewsFragmentState> getStateLiveData() {
        return state;
    }

    public void sendIntent(NewsFragmentIntent intent) {
        if(intent instanceof RefreshNewsIntent) {
            refreshNews();
        }
        else if(intent instanceof AddNewsIntent) {
            addNews((AddNewsIntent) intent);
        }
        else if(intent instanceof DeleteNewsIntent) {
            processDeleteNews((DeleteNewsIntent)intent);
        }
        else {
            throw new IntentIsNotSupportedException(intent.getClass().getName());
        }
    }

    private void processDeleteNews(DeleteNewsIntent intent) {
        newsDataSource.deleteNews(intent.getNews())
                .onComplete(new Deferrable.OnCompleteCallback<ServerResponse>() {
                    @Override
                    public void onComplete(ServerResponse result) {
                        sendIntent(new RefreshNewsIntent());
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<ServerResponse>() {
                    @Override
                    public void onException(Throwable t) {
                        state.postValue(new ExceptionState(t));
                    }
                });
    }

    private void addNews(AddNewsIntent intent) {
        NewsFragmentState currentState = state.getValue();
        if(currentState instanceof ListNewsState) {
            ListNewsState listState = (ListNewsState) currentState;
            List<News> oldList = listState.getNews();
            List<News> newList = new ArrayList<News>(oldList.size() + 1);
            newList.add(intent.getNews());
            state.postValue(new ListNewsState(newList));
        }
        else {
            state.postValue(new ListNewsState(Collections.singletonList(intent.getNews())));
        }
    }

    private void refreshNews() {
        newsDataSource.refreshNews();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
