package com.employersapps.employersapp.presentation.notification_details_fragment.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.employersapps.core.domain.AdminNotification;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.UserNotification;
import com.employersapps.core.utils.StateIsNotSupported;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.FragmentNotificationDetailsBinding;
import com.employersapps.employersapp.framework.domain.ParcelableEmployer;
import com.employersapps.employersapp.framework.domain.ParcelableUserNotification;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.date_time_picker.ui.DateTimePicker;
import com.employersapps.employersapp.presentation.notification_details_fragment.intent.AddAdminNotificationIntent;
import com.employersapps.employersapp.presentation.notification_details_fragment.intent.AddNotificationIntent;
import com.employersapps.employersapp.presentation.notification_details_fragment.intent.EditNotificationIntent;
import com.employersapps.employersapp.presentation.notification_details_fragment.state.NotificationDetailsState;
import com.employersapps.employersapp.presentation.notification_details_fragment.state.TransactionState;
import com.employersapps.employersapp.presentation.notification_details_fragment.viewmodel.NotificationDetailsViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class NotificationDetailsFragment extends Fragment {

    public static final String KEY_EDIT_NOTIFICATION = "keyEditNotification";
    public static final String EXTRA_TO_EMPLOYER = "keyToEmployer";

    private boolean isToOtherEmployer;

    private boolean isToEdit;
    private UserNotification toEdit;

    private NotificationDetailsViewModel viewModel;
    private FragmentNotificationDetailsBinding binding;

    private LocalDateTime currentFireTime;

    private Employer targetEmployer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(NotificationDetailsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationDetailsBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initButtons();
        initEdit();
        initToOtherEmployer();
        observeViewModel();
    }

    private void initToOtherEmployer() {
        Bundle args = getArguments();

        if(args == null) {
            isToOtherEmployer = false;
        }
        else {
            Parcelable parcelable = args.getParcelable(EXTRA_TO_EMPLOYER);
            if(parcelable == null) {
                isToOtherEmployer = false;
            }
            else {
                isToOtherEmployer = true;
                targetEmployer = ((ParcelableEmployer)parcelable).getEmployer();
            }
        }
    }

    private void initEdit() {

        Bundle args = getArguments();

        if(args == null) {
            isToEdit = false;
        }
        else {
            Parcelable parcelable = args.getParcelable(KEY_EDIT_NOTIFICATION);

            if(parcelable == null) {
                isToEdit = false;
            }
            else {
                ParcelableUserNotification parcelableUserNotification = (ParcelableUserNotification)
                    parcelable;
                isToEdit = true;
                toEdit = parcelableUserNotification.getUserNotification();
                binding.edText.setText(toEdit.getText());
                setCurrentFireTime(toEdit.getFireDateTime());
            }
        }

    }

    private void initButtons() {
        binding.txtLayoutDateTime.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateTimePicker dateTimePicker = DateTimePicker.newInstance(LocalDateTime.now());

                dateTimePicker.show(getChildFragmentManager(), null);

                dateTimePicker.setOnAcceptClickedCallback(new DateTimePicker.OnAcceptClickedCallback() {
                    @Override
                    public void onAcceptClicked(LocalDateTime dateTime) {
                        setCurrentFireTime(dateTime);
                    }
                });
            }
        });
    }

    private void setCurrentFireTime(LocalDateTime dateTime) {
        currentFireTime = dateTime;
        binding.edDateTime.setError(null);
        binding.edDateTime.setText(dateTime.format(
                DateTimeFormatter.ofLocalizedDateTime(
                        FormatStyle.MEDIUM, FormatStyle.SHORT)
        ));
    }

    private void observeViewModel() {
        viewModel.getState().observe(getViewLifecycleOwner(), new Observer<NotificationDetailsState>() {
            @Override
            public void onChanged(NotificationDetailsState notificationDetailsState) {
                render(notificationDetailsState);
            }
        });
    }

    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId() == R.id.menu_accept) {
                    saveChanges();
                    return true;
                }

                return false;
            }
        });

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot())
                        .navigateUp();
            }
        });
    }


    private void saveChanges() {

        String text = binding.edText.getText().toString();


        if(currentFireTime == null) {
            binding.txtLayoutDateTime.setError(
                    requireContext().getString(R.string.set_notification_fire_time)
            );
            return;
        }


        if(isToEdit) {
            UserNotification toSave = new UserNotification(
                    toEdit.getId(),
                    text,
                    currentFireTime,
                    false
            );
            viewModel.sendIntent(new EditNotificationIntent(toSave));
        }
        else if(isToOtherEmployer) {

            AdminNotification adminNotification = new AdminNotification(
                    (int)targetEmployer.getId(),
                    text,
                    currentFireTime
            );

            viewModel.sendIntent(new AddAdminNotificationIntent(adminNotification));
        }
        else {
            UserNotification userNotification = new UserNotification(text, currentFireTime);

            viewModel.sendIntent(new AddNotificationIntent(userNotification));
        }
    }

    private void render(NotificationDetailsState state) {
        if(state instanceof TransactionState.TransactionSuccessState) {
            renderSuccessState();
        }
        else if(state instanceof TransactionState.TransactionErrorState) {
            renderErrorState((TransactionState.TransactionErrorState)state);
        }
        else {
            throw new StateIsNotSupported(state.getClass().getName());
        }
    }

    private void renderSuccessState() {
        Navigation.findNavController(binding.getRoot())
                .navigateUp();
    }

    private void renderErrorState(TransactionState.TransactionErrorState errorState) {
        ToastUtil.showExceptionMessage(
                requireContext(),
                errorState.getThrowable()
        );
    }
}
