package com.employersapps.employersapp.presentation.main_fragment.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.employersapps.core.data.FmsTokenDataSource;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.FragmentMainBinding;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private FmsTokenDataSource fmsTokenDataSource;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCloudMessaging();
        EmployersApp employersApp = (EmployersApp) requireActivity().getApplication();
        fmsTokenDataSource = employersApp.getAppComponent().getFmsTokenDataSource();
    }


    private void initCloudMessaging() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()) {
                            Log.i("FmsToken", "Token generated");
                            fmsTokenDataSource.updateToken(task.getResult());
                        }
                        else {
                            Log.e("FmsToken",
                                    task.getException().getMessage(),
                                    task.getException());
                        }
                     }
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initNavigation();
    }

    private void initNavigation() {

        NavController navController = Navigation.findNavController(
                requireActivity(),
                R.id.main_fragment_nav_host
        );

        NavigationUI.setupWithNavController(
                binding.bottomNavigation,
                navController
        );
    }
}
