package com.employersapps.employersapp.presentation.marked_messages_fragment.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.MarkedMessage;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.FragmentMarkedMessagesBinding;
import com.employersapps.employersapp.framework.util.PaddingDecorator;
import com.employersapps.employersapp.presentation.chat_fragment.adapter.ChatAdapter;
import com.employersapps.employersapp.presentation.marked_messages_fragment.adapter.MarkedMessageAdapter;
import com.employersapps.employersapp.presentation.marked_messages_fragment.viewmodel.MarkedMessagesViewModel;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MarkedMessagesFragment extends Fragment {

    private FragmentMarkedMessagesBinding binding;
    private MarkedMessagesViewModel viewModel;
    private MarkedMessageAdapter adapter;

    private LocalDate dateFilter = null;
    private String query = null;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(MarkedMessagesViewModel.class);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = FragmentMarkedMessagesBinding.inflate(
                inflater,
                container,
                false
        );


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initRecyclerView();
        initFilters();
        observeViewModel();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(
                        v
                ).navigateUp();
            }
        });
    }

    private void initFilters() {
        binding.btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocalDate now;

                if(dateFilter == null) {
                    now = LocalDate.now();
                }
                else {
                    now = dateFilter;
                }

                DatePickerDialog dialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dateFilter = LocalDate.of(
                                        year,
                                        month + 1,
                                        dayOfMonth
                                );
                                binding.tvDate.setText("Selected date: "
                                        + dateFilter.format(
                                        DateTimeFormatter.ISO_DATE
                                ));
                                updateFilter(dateFilter, query);
                            }
                        },
                        now.getYear(),
                        now.getMonthValue() - 1,
                        now.getDayOfMonth()
                );

                dialog.show();

            }
        });

        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                query = s.toString();

                updateFilter(dateFilter, query);
            }
        });
    }

    private void updateFilter(LocalDate dateFilter, String query) {
        viewModel.updateFilter(dateFilter, query);
    }

    private void observeViewModel() {
        viewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<List<MarkedMessage>>() {
            @Override
            public void onChanged(List<MarkedMessage> markedMessages) {
                adapter.setItems(markedMessages);
            }
        });
    }


    private void initRecyclerView() {

        adapter =
                new MarkedMessageAdapter();

        RecyclerView rvMessages = binding.rvMarkedMessages;

        rvMessages.setLayoutManager(new LinearLayoutManager(
                requireContext()
        ));

        int padding = requireContext()
                .getResources()
                .getDimensionPixelSize(R.dimen.default_padding) / 2;

        PaddingDecorator decorator = new PaddingDecorator(
                padding,
                padding,
                0,
                0
        );

        rvMessages.addItemDecoration(decorator);

        rvMessages.setAdapter(adapter);
    }
}
