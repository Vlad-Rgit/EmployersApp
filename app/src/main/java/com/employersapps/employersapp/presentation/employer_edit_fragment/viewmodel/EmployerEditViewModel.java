package com.employersapps.employersapp.presentation.employer_edit_fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.employersapps.core.data.EditEmployersDataSource;
import com.employersapps.core.data.EmployersDataSource;
import com.employersapps.core.data.PostDataSource;
import com.employersapps.core.data.RolesDataSource;
import com.employersapps.core.domain.Post;
import com.employersapps.core.domain.Role;
import com.employersapps.core.domain.network.ServerResponse;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.employer_edit_fragment.intent.AddEmployerIntent;
import com.employersapps.employersapp.presentation.employer_edit_fragment.intent.EmployerEditIntent;
import com.employersapps.employersapp.presentation.employer_edit_fragment.intent.UpdateEmployerIntent;
import com.employersapps.employersapp.presentation.employer_edit_fragment.state.EmployerEditState;
import com.employersapps.employersapp.presentation.employer_edit_fragment.state.SaveState;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

public class EmployerEditViewModel extends AndroidViewModel {


    private final MutableLiveData<List<Role>> rolesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Post>> postsLiveData = new MutableLiveData<>();
    private final MutableLiveData<EmployerEditState> state = new MutableLiveData<>();


    private PostDataSource postDataSource;
    private RolesDataSource rolesDataSource;
    private EditEmployersDataSource editEmployersDataSource;

    public EmployerEditViewModel(@NonNull @NotNull Application application) {
        super(application);
        EmployersApp employersApp = (EmployersApp)application;
        employersApp.getAppComponent().inject(this);

        rolesDataSource.getRoles().onComplete(new Deferrable.OnCompleteCallback<List<Role>>() {
            @Override
            public void onComplete(List<Role> result) {
                rolesLiveData.postValue(result);
            }
        }).onException(new Deferrable.OnExceptionCallback<List<Role>>() {
            @Override
            public void onException(Throwable t) {
                ToastUtil.showExceptionMessage(
                        employersApp.getApplicationContext(),
                        t
                );
            }
        });

        postDataSource.getPosts().onComplete(new Deferrable.OnCompleteCallback<List<Post>>() {
            @Override
            public void onComplete(List<Post> result) {
                postsLiveData.postValue(result);
            }
        }).onException(new Deferrable.OnExceptionCallback<List<Post>>() {
            @Override
            public void onException(Throwable t) {
                ToastUtil.showExceptionMessage(
                        employersApp.getApplicationContext(),
                        t
                );
            }
        });
    }


    public void sendIntent(EmployerEditIntent intent) {
        if(intent instanceof AddEmployerIntent) {
            AddEmployerIntent i = (AddEmployerIntent)intent;
            editEmployersDataSource.addEmployer(
                    i.postEmployer,
                    i.photoUri
            ).onComplete(new Deferrable.OnCompleteCallback<ServerResponse>() {
                @Override
                public void onComplete(ServerResponse result) {
                    state.postValue(new SaveState.Succes(result));
                }
            }).onException(new Deferrable.OnExceptionCallback<ServerResponse>() {
                @Override
                public void onException(Throwable t) {
                    state.postValue(new SaveState.Error(t));
                }
            });
        }
        else if(intent instanceof UpdateEmployerIntent) {
            UpdateEmployerIntent i = (UpdateEmployerIntent)intent;
            editEmployersDataSource.editEmployer(i.postEmployer, i.photoUri)
                    .onComplete(new Deferrable.OnCompleteCallback<ServerResponse>() {
                @Override
                public void onComplete(ServerResponse result) {
                    state.postValue(new SaveState.Succes(result));
                }
            }).onException(new Deferrable.OnExceptionCallback<ServerResponse>() {
                @Override
                public void onException(Throwable t) {
                    state.postValue(new SaveState.Error(t));
                }
            });
        }
    }

    public MutableLiveData<List<Role>> getRolesLiveData() {
        return rolesLiveData;
    }

    public MutableLiveData<List<Post>> getPostsLiveData() {
        return postsLiveData;
    }

    public MutableLiveData<EmployerEditState> getState() {
        return state;
    }

    @Inject
    public void setPostDataSource(PostDataSource postDataSource) {
        this.postDataSource = postDataSource;
    }

    @Inject
    public void setRolesDataSource(RolesDataSource rolesDataSource) {
        this.rolesDataSource = rolesDataSource;
    }

    @Inject
    public void setEditEmployersDataSource(EditEmployersDataSource editEmployersDataSource) {
        this.editEmployersDataSource = editEmployersDataSource;
    }
}
