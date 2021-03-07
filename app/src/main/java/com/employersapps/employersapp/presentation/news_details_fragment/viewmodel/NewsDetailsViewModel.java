package com.employersapps.employersapp.presentation.news_details_fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.NewsDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.News;
import com.employersapps.core.domain.NewsFile;
import com.employersapps.core.domain.NewsImage;
import com.employersapps.core.domain.network.NewsFileNetwork;
import com.employersapps.core.domain.network.NewsImageNetwork;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.core.utils.IntentIsNotSupportedException;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.employer_edit_fragment.intent.UpdateEmployerIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddMultipleImageIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddMultipleNewsFilesIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddNewsFileIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddNewsImageIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddNewsIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.NewsDetailsIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.RemoveNewsFileIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.RemoveNewsImageIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.UpdateNewsIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.state.ExceptionState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsAddedState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsDetailsState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsFilesState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsImagesListState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.SendingState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class NewsDetailsViewModel extends AndroidViewModel {

    private List<NewsImage> newsImages = Collections.emptyList();
    private List<NewsFile> newsFiles = Collections.emptyList();
    private NewsDataSource newsDataSource;
    private UserDataSource userDataSource;

    private MutableLiveData<NewsDetailsState> state = new MutableLiveData<>();

    public NewsDetailsViewModel(@NonNull Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp)application;
        employersApp.getAppComponent().inject(this);
    }

    @Inject
    public void setNewsDataSource(NewsDataSource newsDataSource) {
        this.newsDataSource = newsDataSource;
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    public LiveData<NewsDetailsState> getStateLiveData() {
        return state;
    }

    public int imagesCount() {
        return newsImages.size();
    }

    public void sendIntent(NewsDetailsIntent intent) {
        if(intent instanceof AddNewsIntent) {
            processAddNewsIntent((AddNewsIntent)intent);
        }
        else if(intent instanceof AddNewsImageIntent) {
            processAddNewsImageIntent((AddNewsImageIntent)intent);
        }
        else if(intent instanceof RemoveNewsImageIntent) {
            processRemoveNewsImageIntent((RemoveNewsImageIntent)intent);
        }
        else if(intent instanceof AddMultipleImageIntent) {
            processAddMultipleImagesIntent((AddMultipleImageIntent)intent);
        }
        else if(intent instanceof AddNewsFileIntent) {
            processAddNewsFile((AddNewsFileIntent)intent);
        }
        else if(intent instanceof AddMultipleNewsFilesIntent) {
            processAddMultipleNewsFile((AddMultipleNewsFilesIntent)intent);
        }
        else if(intent instanceof RemoveNewsFileIntent) {
            processRemoveNewsFileIntent((RemoveNewsFileIntent)intent);
        }
        else if(intent instanceof UpdateNewsIntent) {
            processUpdateNewsIntent((UpdateNewsIntent)intent);
        }
        else {
            throw new IntentIsNotSupportedException(intent.getClass().getName());
        }
    }

    private void processUpdateNewsIntent(UpdateNewsIntent intent) {
        newsDataSource.updateNewsWithFiles(
                intent.getNews(),
                newsImages,
                newsFiles
        ).onComplete(new Deferrable.OnCompleteCallback<News>() {
            @Override
            public void onComplete(News result) {
                state.postValue(new NewsAddedState(result));
            }
        }).onException(new Deferrable.OnExceptionCallback<News>() {
            @Override
            public void onException(Throwable t) {
                state.postValue(new ExceptionState(t));
            }
        });
    }

    public long getCurrentUserId() {
        return userDataSource.getCurrentUser().getId();
    }

    private void processAddNewsFile(AddNewsFileIntent intent) {
        reduceNewsFiles(intent.getNewsFile());
        state.postValue(new NewsFilesState(newsFiles));
    }

    private void processAddMultipleNewsFile(AddMultipleNewsFilesIntent intent) {
        reduceNewsFiles(intent.getNewsFiles());
        state.postValue(new NewsFilesState(newsFiles));
    }

    private void reduceNewsFiles(NewsFile newItem) {
        List<NewsFile> files = new ArrayList<>(newsFiles.size() + 1);
        files.addAll(newsFiles);
        files.add(newItem);
        newsFiles = Collections.unmodifiableList(files);
    }

    private void reduceNewsFiles(List<NewsFile> newFiles) {
        List<NewsFile> files = new ArrayList<>(newsFiles.size() + newFiles.size());
        files.addAll(newsFiles);
        files.addAll(newFiles);
        newsFiles = Collections.unmodifiableList(files);
    }

    private void processAddMultipleImagesIntent(AddMultipleImageIntent intent) {

        List<NewsImage> newListState = new ArrayList<>(newsImages.size()
                + intent.getNewsImages().size());

        newListState.addAll(newsImages);
        newListState.addAll(intent.getNewsImages());

        newsImages = Collections.unmodifiableList(newListState);

        state.postValue(new NewsImagesListState(newsImages));
    }

    public void refreshNewsImages() {
        state.postValue(new NewsImagesListState(newsImages));
    }

    private void processAddNewsImageIntent(AddNewsImageIntent intent) {

        List<NewsImage> newListState = new ArrayList<>(newsImages.size() + 1);

        newListState.addAll(newsImages);
        newListState.add(intent.getNewsImage());

        newsImages = Collections.unmodifiableList(newListState);

        state.postValue(new NewsImagesListState(newListState));
    }

    private void processRemoveNewsImageIntent(RemoveNewsImageIntent intent) {

        NewsImage toDelete = intent.getNewsImage();

        List<NewsImage> newListState = new ArrayList<>(newsImages.size() - 1);

        for(NewsImage news : newsImages) {
            if(!toDelete.equals(news)) {
                newListState.add(news);
            }
        }

        if(toDelete.isNetwork()) {
            newsDataSource.deleteNewsPhoto(new NewsImageNetwork(
                    toDelete.getUri()
            ));
        }

        newsImages = Collections.unmodifiableList(newListState);

        state.postValue(new NewsImagesListState(newsImages));
    }

    private void processRemoveNewsFileIntent(RemoveNewsFileIntent intent) {

        List<NewsFile> files = new ArrayList<>(newsFiles.size() - 1);
        NewsFile toDelete = intent.getNewsFile();

        for(NewsFile file : newsFiles) {
            if(!file.areItemsTheSame(toDelete)) {
                files.add(file);
            }
        }

        if(toDelete.isNetwork()) {
            newsDataSource.deleteNewsFile(new NewsFileNetwork(
                    toDelete.getName()
            ));
        }

        newsFiles = Collections.unmodifiableList(files);
        state.postValue(new NewsFilesState(newsFiles));
    }


    private void processAddNewsIntent(AddNewsIntent intent) {
        state.postValue(new SendingState());
         newsDataSource.addNewsWithFiles(intent.getNews(), newsImages, newsFiles)
                 .onComplete(new Deferrable.OnCompleteCallback<News>() {
                     @Override
                     public void onComplete(News result) {
                         state.postValue(new NewsAddedState(result));
                     }
                 })
                 .onException(new Deferrable.OnExceptionCallback<News>() {
                     @Override
                     public void onException(Throwable t) {
                         state.postValue(new ExceptionState(t));
                     }
                 });
    }
}
