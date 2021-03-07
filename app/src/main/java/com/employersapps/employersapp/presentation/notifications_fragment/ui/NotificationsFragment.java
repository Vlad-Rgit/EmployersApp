package com.employersapps.employersapp.presentation.notifications_fragment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.employersapps.core.domain.UserNotification;
import com.employersapps.core.utils.StateIsNotSupported;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.FragmentNotificationsBinding;
import com.employersapps.employersapp.framework.domain.ParcelableUserNotification;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.news_fragment.states.NewsFragmentState;
import com.employersapps.employersapp.presentation.notification_details_fragment.ui.NotificationDetailsFragment;
import com.employersapps.employersapp.presentation.notifications_fragment.adapter.NotificationAdapter;
import com.employersapps.employersapp.presentation.notifications_fragment.intent.RemoveNotificationIntent;
import com.employersapps.employersapp.presentation.notifications_fragment.state.ListState;
import com.employersapps.employersapp.presentation.notifications_fragment.state.NotificationsFragmentState;
import com.employersapps.employersapp.presentation.notifications_fragment.viewmodel.NotificationsViewModel;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationsViewModel viewModel;
    private NotificationAdapter notificationAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(NotificationsViewModel.class);

        notificationAdapter = new NotificationAdapter();

        notificationAdapter.setOnRemoveClick(new NotificationAdapter.OnNotificationClick() {
            @Override
            public void onNotificationClick(UserNotification notification) {
                removeNotification(notification);
            }
        });

        notificationAdapter.setOnEditClick(new NotificationAdapter.OnNotificationClick() {
            @Override
            public void onNotificationClick(UserNotification notification) {
                editNotification(notification);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        initButtons();
        observeViewModel();
    }

    private void removeNotification(UserNotification notification) {
        viewModel.sendIntent(new RemoveNotificationIntent(notification));
    }

    private void initRecyclerView() {

        RecyclerView rvNotifications = binding.rvNotifications;

        rvNotifications.setHasFixedSize(true);
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotifications.setAdapter(notificationAdapter);
    }

    private void editNotification(UserNotification userNotification) {
        Bundle args = new Bundle();
        ParcelableUserNotification parcelable = new ParcelableUserNotification(userNotification);
        args.putParcelable(NotificationDetailsFragment.KEY_EDIT_NOTIFICATION, parcelable);
        Navigation.findNavController(
                requireActivity(),
                R.id.nav_host
        ).navigate(
                R.id.action_mainFragment_to_notificationDetailsFragment,
                args
        );
    }

    private void initButtons() {
        binding.btnAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(
                        requireActivity(),
                        R.id.nav_host
                ).navigate(
                        R.id.action_mainFragment_to_notificationDetailsFragment
                );
            }
        });
    }

    private void observeViewModel() {
        viewModel.getState().observe(getViewLifecycleOwner(), new Observer<NotificationsFragmentState>() {
            @Override
            public void onChanged(NotificationsFragmentState notificationsFragmentState) {
                render(notificationsFragmentState);
            }
        });
    }

    private void render(NotificationsFragmentState state) {
        if(state instanceof ListState.Success) {
            renderListSuccessState((ListState.Success)state);
        }
        else if(state instanceof ListState.Error) {
            renderListErrorState((ListState.Error)state);
        }
        else {
            throw new StateIsNotSupported(state.getClass().getName());
        }
    }

    private void renderListSuccessState(ListState.Success state) {
        notificationAdapter.setItems(state.getList());
    }

    private void renderListErrorState(ListState.Error state) {
        ToastUtil.showExceptionMessage(
                requireContext(),
                state.getThrowable()
        );
    }
}
