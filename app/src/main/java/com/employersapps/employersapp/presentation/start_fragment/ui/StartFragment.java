package com.employersapps.employersapp.presentation.start_fragment.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.employersapps.core.data.SaveAuthHelper;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.network.RequestResult;
import com.employersapps.core.domain.User;
import com.employersapps.core.domain.UserLogin;
import com.employersapps.core.utils.Deferrable;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.di.components.AppComponent;

public class StartFragment extends Fragment {


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        EmployersApp app = (EmployersApp) requireActivity().getApplication();
        AppComponent appComponent = app.getAppComponent();

        SaveAuthHelper saveAuthHelper = appComponent.getSaveAuthHelper();

        if(saveAuthHelper.isAuthenticated()) {
            UserDataSource userDataSource = appComponent.getUserDataSource();
            skipLoginForm(saveAuthHelper, userDataSource);
        }
        else {
            goToLoginForm();
        }


    }

    private void skipLoginForm(SaveAuthHelper authHelper, UserDataSource userDataSource) {

        UserLogin userLogin = new UserLogin(
                authHelper.getLogin(),
                authHelper.getPassword()
        );

        userDataSource.login(userLogin)
                .onComplete(new Deferrable.OnCompleteCallback<RequestResult<User>>() {
                    @Override
                    public void onComplete(RequestResult<User> result) {
                        Navigation.findNavController(
                                requireActivity(),
                                R.id.nav_host
                        ).navigate(
                                R.id.action_startFragment_to_mainFragment
                        );
                    }
                })
                .onException(new Deferrable.OnExceptionCallback<RequestResult<User>>() {
                    @Override
                    public void onException(Throwable t) {
                        goToLoginForm();
                    }
                });
    }

    private void goToLoginForm() {
        Navigation.findNavController(
                requireActivity(),
                R.id.nav_host
        ).navigate(
                R.id.action_startFragment_to_loginFragment
        );
    }
}
