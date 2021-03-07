package com.employersapps.employersapp.presentation.employers_choice_list.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.employersapps.core.domain.Employer;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.FragmentEmployersChoiceListBinding;
import com.employersapps.employersapp.framework.domain.ParcelableEmployer;
import com.employersapps.employersapp.framework.util.PaddingDecorator;
import com.employersapps.employersapp.presentation.employers_choice_list.adapters.EmployerMultipleChoiceAdapter;
import com.employersapps.employersapp.presentation.employers_choice_list.viewmodel.EmployersChoiceListViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EmployersChoiceListFragment extends Fragment {

    public static final String KEY_CHOSEN_EMPLOYERS = "keyChosenEmployers";

    private EmployersChoiceListViewModel viewModel;
    private FragmentEmployersChoiceListBinding binding;
    private EmployerMultipleChoiceAdapter adapter;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(EmployersChoiceListViewModel.class);


        viewModel.fetchEmployers();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentEmployersChoiceListBinding.inflate(
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
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getEmployers().observe(getViewLifecycleOwner(), new Observer<List<Employer>>() {
            @Override
            public void onChanged(List<Employer> employers) {
                adapter.setItems(employers);
            }
        });
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(
                        binding.getRoot()
                ).navigateUp();
            }
        });
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_ok) {
                    Bundle bundle = new Bundle();
                    List<Employer> selectedEmployers = adapter.getSelectedEmployers();
                    ArrayList<ParcelableEmployer> parcelables = new ArrayList<>(selectedEmployers.size());
                    for(Employer e : selectedEmployers) {
                        parcelables.add(new ParcelableEmployer(e));
                    }

                    bundle.putParcelableArrayList(KEY_CHOSEN_EMPLOYERS, parcelables);
                    getParentFragmentManager().setFragmentResult(
                            KEY_CHOSEN_EMPLOYERS,
                            bundle
                    );

                    Navigation.findNavController(binding.getRoot())
                            .navigateUp();

                    return true;
                }
                return false;
            }
        });
    }

    private void initRecyclerView() {
        adapter = new EmployerMultipleChoiceAdapter();
        RecyclerView rvEmployers = binding.rvEmployers;
        rvEmployers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvEmployers.setAdapter(adapter);

        int padding = requireContext()
                .getResources()
                .getDimensionPixelSize(R.dimen.default_padding);

        rvEmployers.addItemDecoration(new PaddingDecorator(
                0, padding, 0, 0
        ));
    }

}
