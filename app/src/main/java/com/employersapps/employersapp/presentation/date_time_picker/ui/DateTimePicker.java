package com.employersapps.employersapp.presentation.date_time_picker.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.DateTimePickerBinding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimePicker extends DialogFragment {

    private DateTimePickerPageProvider pageProvider;

    @FunctionalInterface
    public interface OnDateSelectedCallback {
        void onDateSelected(LocalDate date);
    }

    @FunctionalInterface
    public interface OnTimeSelectedCallback {
        void onTimeSelected(LocalTime time);
    }

    @FunctionalInterface
    public interface OnAcceptClickedCallback {
        void onAcceptClicked(LocalDateTime dateTime);
    }

    @FunctionalInterface
    public interface OnCancelClickedCallback {
        void onCancelClicked();
    }

    private static final String KEY_INIT_DATE_TIME = "keyInitDateTime";

    public static DateTimePicker newInstance(LocalDateTime initDateTime) {
        DateTimePicker dateTimePicker = new DateTimePicker();
        Bundle args = new Bundle();
        args.putSerializable(KEY_INIT_DATE_TIME, initDateTime);
        dateTimePicker.setArguments(args);
        return dateTimePicker;
    }

    private DateTimePickerBinding binding;
    private OnAcceptClickedCallback onAcceptClickedCallback;
    private OnCancelClickedCallback onCancelClickedCallback;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DateTimePickerBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabs();
        initButtons();
    }

    private void initButtons() {
        binding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onAcceptClickedCallback != null) {
                    LocalDateTime dateTime = LocalDateTime.of(
                            pageProvider.getCurrentDate(),
                            pageProvider.getCurrentTime()
                    );
                    onAcceptClickedCallback.onAcceptClicked(dateTime);
                }

                dismiss();
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelClickedCallback != null) {
                    onCancelClickedCallback.onCancelClicked();
                }
                dismiss();
            }
        });
    }

    private void initTabs() {

        Bundle args = getArguments();
        LocalDateTime initDateTime;

        if(args == null) {
            initDateTime = LocalDateTime.now();
        }
        else {
            initDateTime = (LocalDateTime) args.getSerializable(KEY_INIT_DATE_TIME);
            if(initDateTime == null) {
                initDateTime = LocalDateTime.now();
            }
        }

        pageProvider = new DateTimePickerPageProvider(
                requireContext(),
                getChildFragmentManager(),
                initDateTime.toLocalDate(),
                initDateTime.toLocalTime()
        );

        binding.viewPager.setAdapter(pageProvider);

        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    public void setOnAcceptClickedCallback(OnAcceptClickedCallback onAcceptClickedCallback) {
        this.onAcceptClickedCallback = onAcceptClickedCallback;
    }

    public void setOnCancelClickedCallback(OnCancelClickedCallback onCancelClickedCallback) {
        this.onCancelClickedCallback = onCancelClickedCallback;
    }

    public static class DateTimePickerPageProvider extends FragmentStatePagerAdapter {

        private LocalDate currentDate;
        private LocalTime currentTime;
        private final Context context;

        public DateTimePickerPageProvider(Context context,
                                          FragmentManager fm,
                                          LocalDate initDate,
                                          LocalTime initTime) {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.context = context;
            this.currentDate = initDate;
            this.currentTime = initTime;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    DatePickerFragment dateFragment = DatePickerFragment.newInstance(currentDate);
                    dateFragment.setOnDateSelected(date -> currentDate = date);
                    return dateFragment;
                case 1:
                    TimePickerFragment timeFragment = TimePickerFragment.newInstance(currentTime);
                    timeFragment.setOnTimeSelected(time -> currentTime = time);
                    return timeFragment;
                default: throw new IllegalArgumentException(
                        "No fragment at position: " + position);
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getString(R.string.date);
                case 1:
                    return context.getString(R.string.time);
                default: throw new IllegalArgumentException(
                        "No title at position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public LocalDate getCurrentDate() {
            return currentDate;
        }

        public LocalTime getCurrentTime() {
            return currentTime;
        }
    }

    public static class DatePickerFragment extends Fragment {

        private OnDateSelectedCallback onDateSelected;

        private static final String KEY_INIT_DATE = "keyInitDate";

        public static DatePickerFragment newInstance(LocalDate initDate) {
            Bundle args = new Bundle();
            args.putSerializable(KEY_INIT_DATE, initDate);
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setArguments(args);
            return fragment;
        }


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {


            Bundle args = getArguments();
            LocalDate initDate;

            if(args == null) {
                initDate = LocalDate.now();
            }
            else {
                initDate = (LocalDate) args.getSerializable(KEY_INIT_DATE);
                if(initDate == null) {
                    initDate = LocalDate.now();
                }
            }


            DatePicker datePicker = new DatePicker(requireContext());

            datePicker.init(initDate.getYear(),
                    initDate.getMonthValue() - 1,
                    initDate.getDayOfMonth(),
                    new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            if(onDateSelected != null) {
                                LocalDate date = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                                onDateSelected.onDateSelected(date);
                            }
                        }
                    });

            return datePicker;
        }

        public void setOnDateSelected(OnDateSelectedCallback onDateSelected) {
            this.onDateSelected = onDateSelected;
        }
    }


    public static class TimePickerFragment extends Fragment {

        private OnTimeSelectedCallback onTimeSelected;

        private static final String KEY_INIT_TIME = "keyInitDate";

        public static TimePickerFragment newInstance(LocalTime initTime) {
            Bundle args = new Bundle();
            args.putSerializable(KEY_INIT_TIME, initTime);
            TimePickerFragment fragment = new TimePickerFragment();
            fragment.setArguments(args);
            return fragment;
        }


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {


            Bundle args = getArguments();
            LocalTime initTime;

            if(args == null) {
                initTime = LocalTime.now();
            }
            else {
                initTime = (LocalTime) args.getSerializable(KEY_INIT_TIME);
                if(initTime == null) {
                    initTime = LocalTime.now();
                }
            }


            TimePicker timePicker = new TimePicker(requireContext());

            timePicker.setCurrentHour(initTime.getHour());
            timePicker.setCurrentMinute(initTime.getMinute());

            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    if(onTimeSelected != null) {
                        LocalTime time = LocalTime.of(hourOfDay, minute);
                        onTimeSelected.onTimeSelected(time);
                    }
                }
            });

            return timePicker;
        }


        public void setOnTimeSelected(OnTimeSelectedCallback onTimeSelected) {
            this.onTimeSelected = onTimeSelected;
        }
    }

}
