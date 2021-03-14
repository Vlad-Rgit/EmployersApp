package com.employersapps.employersapp.presentation.profile_fragment;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.EditEmployersDataSource;
import com.employersapps.core.data.EmployerChangesDataSource;
import com.employersapps.core.data.EmployersDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.network.PostEmployer;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.EmployerChangedState;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.EmployerDetailsState;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ProfileViewModel extends AndroidViewModel {

    private Employer employer;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private EmployerChangesDataSource employerChangesDataSource;
    private EmployersDataSource employersDataSource;

    private UserDataSource userDataSource;
    private EditEmployersDataSource editEmployersDataSource;

    private MutableLiveData<EmployerDetailsState> state = new MutableLiveData<>();


    public ProfileViewModel(@NonNull Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp)application;
        employersApp.getAppComponent().inject(this);

        long userId = userDataSource.getCurrentUser().getId();

        employersDataSource.getEmployer(userId)
                .onComplete(new Deferrable.OnCompleteCallback<Employer>() {
                    @Override
                    public void onComplete(Employer result) {
                        setEmployer(result);
                        state.postValue(new EmployerChangedState.Success(result));
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<Employer>() {
                    @Override
                    public void onException(Throwable t) {
                        state.postValue(new EmployerChangedState.Error(t));
                    }
                });
    }

    @Inject
    public void setEditEmployersDataSource(EditEmployersDataSource editEmployersDataSource) {
        this.editEmployersDataSource = editEmployersDataSource;
    }

    @Inject
    public void setEmployersDataSource(EmployersDataSource employersDataSource) {
        this.employersDataSource = employersDataSource;
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    @Inject
    public void setEmployerChangesDataSource(EmployerChangesDataSource employerChangesDataSource) {
        this.employerChangesDataSource = employerChangesDataSource;
    }

    public Employer getEmployer() {
        return employer;
    }

    private void setEmployer(Employer employer) {
        this.employer = employer;
        employerChangesDataSource.init(employer);

        Disposable disposable =  employerChangesDataSource
                .getChanges()
                .subscribeWith(new DisposableObserver<Employer>() {

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Employer employer) {
                        state.postValue(new EmployerChangedState.Success(employer));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        state.postValue(new EmployerChangedState.Error(e));
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        compositeDisposable.add(disposable);
    }

    public void updateEmployer(PostEmployer employer) {
        editEmployersDataSource.editEmployer(employer, null);
    }

    public void updatePhoto(Uri uri) {
        editEmployersDataSource.editEmployer(
                new PostEmployer(
                        employer.getId(),
                        employer.getLastName(),
                        employer.getFirstName(),
                        employer.getLogin(),
                        "",
                        employer.getPostId(),
                        employer.getRoleId(),
                        employer.getStartVacationDate(),
                        employer.getEndVacationDate(),
                        employer.getVacationComment(),
                        employer.isEnablePrivateChatNotification(),
                        employer.isEnableGroupChatNotification()
                ),
                uri.toString()
        );
    }

    public void updateStatus(int statusId) {
        employersDataSource.updateStatus(employer.getId(), statusId);
    }

    public LiveData<EmployerDetailsState> getState() {
        return state;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        try {
            compositeDisposable.clear();
            employerChangesDataSource.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
