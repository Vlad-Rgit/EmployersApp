package com.employersapps.employersapp.presentation.employers_fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.EmployerChangesDataSource;
import com.employersapps.core.data.EmployersDataSource;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.IntentIsNotSupportedException;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.framework.data.RetrofitEmployersService;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.employers_fragment.intent.DeleteEmployerIntent;
import com.employersapps.employersapp.presentation.employers_fragment.intent.EmployersFragmentIntent;
import com.employersapps.employersapp.presentation.employers_fragment.intent.RefreshListIntent;
import com.employersapps.employersapp.presentation.employers_fragment.state.EmployersFragmentState;
import com.employersapps.employersapp.presentation.employers_fragment.state.EmployersListState;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployersViewModel extends AndroidViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<EmployersFragmentState> state = new MutableLiveData<>();

    private EmployersDataSource employersDataSource;
    private UserDataSource userDataSource;
    private RetrofitEmployersService retrofitEmployersService;

    public EmployersViewModel(@NonNull Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp) application;
        employersApp.getAppComponent().inject(this);
        observeEmployersDataSource();
    }

    private void observeEmployersDataSource() {

       Disposable employersDisposable = employersDataSource.getEmployers()
               .subscribeWith(new DisposableObserver<List<Employer>>() {
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<Employer> employers) {
                state.postValue(new EmployersListState.Success(employers));
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                state.postValue(new EmployersListState.Error(e));
            }

            @Override
            public void onComplete() {

            }
        });

       compositeDisposable.add(employersDisposable);
    }

    @Inject
    public void setEmployersDataSource(EmployersDataSource employersDataSource) {
        this.employersDataSource = employersDataSource;
    }


    @Inject
    public void setRetrofitEmployersService(RetrofitEmployersService retrofitEmployersService) {
        this.retrofitEmployersService = retrofitEmployersService;
    }

    @Inject
    public void setUserDataSource(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    public boolean isAdmin() {
        return userDataSource.getCurrentUser().getRoleId() == 1;
    }

    public LiveData<EmployersFragmentState> getState() {
        return state;
    }

    public void sendIntent(EmployersFragmentIntent intent) {
        if(intent instanceof RefreshListIntent) {
            processRefreshIntent();
        }
        else if(intent instanceof DeleteEmployerIntent) {
            DeleteEmployerIntent i = (DeleteEmployerIntent) intent;
            retrofitEmployersService.deleteUser(i.toDelete.getId()).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    ToastUtil.showLongToast(
                            getApplication().getApplicationContext(),
                            "User has been deleted"
                    );
                    sendIntent(new RefreshListIntent());
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    ToastUtil.showExceptionMessage(
                            getApplication().getApplicationContext(),
                            t
                    );
                }
            });
        }
        else {
            throw new IntentIsNotSupportedException(intent.getClass().getName());
        }
    }

    public void processRefreshIntent() {
        employersDataSource.refreshEmployers();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
