package com.employersapps.employersapp.presentation.employers_fragment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.employersapps.core.domain.Employer;
import com.employersapps.core.utils.StateIsNotSupported;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.FragmentEmployersBinding;
import com.employersapps.employersapp.framework.domain.ParcelableEmployer;
import com.employersapps.employersapp.framework.util.PaddingDecorator;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.employer_details_fragment.ui.EmployerDetailsFragment;
import com.employersapps.employersapp.presentation.employer_edit_fragment.ui.EmployerEditFragment;
import com.employersapps.employersapp.presentation.employers_fragment.adapter.EmployerAdapter;
import com.employersapps.employersapp.presentation.employers_fragment.intent.DeleteEmployerIntent;
import com.employersapps.employersapp.presentation.employers_fragment.intent.RefreshListIntent;
import com.employersapps.employersapp.presentation.employers_fragment.state.EmployersFragmentState;
import com.employersapps.employersapp.presentation.employers_fragment.state.EmployersListState;
import com.employersapps.employersapp.presentation.employers_fragment.viewmodel.EmployersViewModel;
import com.employersapps.employersapp.presentation.notification_details_fragment.ui.NotificationDetailsFragment;

public class EmployersFragment extends Fragment {

    private FragmentEmployersBinding binding;
    private EmployersViewModel viewModel;
    private EmployerAdapter employerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(EmployersViewModel.class);

        employerAdapter = new EmployerAdapter(requireContext());

        employerAdapter.setOnEmployerClickListener(this::onEmployerClicked);
        employerAdapter.setOnEditClickedListener(this::onEditClicked);
        employerAdapter.setOnRemoveClickedListener(new EmployerAdapter.OnEmployerClickListener() {
            @Override
            public void onEmployerClicked(Employer employer) {
                viewModel.sendIntent(new DeleteEmployerIntent(employer));
            }
        });
        employerAdapter.setOnSendNotificationClickedListener(this::onSendNotificationClicked);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentEmployersBinding.inflate(
                inflater,
                container,
                false
        );



        return binding.getRoot();
    }

    private void onSendNotificationClicked(Employer employer) {
        ParcelableEmployer parcelableEmployer = new ParcelableEmployer(employer);
        Bundle bundle = new Bundle();

        bundle.putParcelable(NotificationDetailsFragment.EXTRA_TO_EMPLOYER, parcelableEmployer);

        Navigation.findNavController(
                requireActivity(),
                R.id.nav_host
        ).navigate(
                R.id.action_mainFragment_to_notificationDetailsFragment,
                bundle
        );
    }

    private void onEditClicked(Employer employer) {

        ParcelableEmployer parcelableEmployer = new ParcelableEmployer(employer);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EmployerEditFragment.EXTRA_EDITABLE_EMPLOYER, parcelableEmployer);

        Navigation.findNavController(
                requireActivity(),
                R.id.nav_host
        ).navigate(
                R.id.action_mainFragment_to_employerEditFragment,
                bundle
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initButtons();
        initRecyclerView();
        observeViewModel();
    }

    private void initButtons() {
        if(viewModel.isAdmin()) {
           binding.btnAddUser.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Navigation.findNavController(
                           requireActivity(),
                           R.id.nav_host
                   ).navigate(
                           R.id.action_mainFragment_to_employerEditFragment
                   );
               }
           });
        }
        else {
            binding.btnAddUser.setVisibility(View.GONE);
        }
    }

    private void onEmployerClicked(Employer employer) {

        Bundle args = new Bundle();

        ParcelableEmployer parcelableEmployer = new ParcelableEmployer(employer);

        args.putParcelable(EmployerDetailsFragment.KEY_EMPLOYER, parcelableEmployer);

        Navigation.findNavController(
                requireActivity(),
                R.id.nav_host
        ).navigate(
                R.id.action_mainFragment_to_employerDetailsFragment,
                args
        );
    }

    private void initRecyclerView() {

        RecyclerView rvEmployers = binding.rvEmployers;

        rvEmployers.setHasFixedSize(true);
        rvEmployers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvEmployers.setAdapter(employerAdapter);

        int padding = (int)requireContext().getResources()
                .getDimension(R.dimen.default_padding) / 2;

        rvEmployers.addItemDecoration(
                new PaddingDecorator(0, padding, 0, 0));

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.sendIntent(new RefreshListIntent());
            }
        });
    }

    private void observeViewModel() {
        viewModel.getState().observe(getViewLifecycleOwner(), new Observer<EmployersFragmentState>() {
            @Override
            public void onChanged(EmployersFragmentState state) {
                render(state);
            }
        });
    }

    private void render(EmployersFragmentState state) {
        binding.swipeRefresh.setRefreshing(false);
        if(state instanceof EmployersListState.Success) {
            renderListState((EmployersListState.Success)state);
        }
        else if(state instanceof EmployersListState.Error) {
            renderExceptionListState((EmployersListState.Error)state);
        }
        else {
            throw new StateIsNotSupported(state.getClass().getName());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.sendIntent(new RefreshListIntent());
    }

    private void renderListState(EmployersListState.Success state) {
        employerAdapter.setItems(state.getResult());
    }

    private void renderExceptionListState(EmployersListState.Error state) {
        ToastUtil.showExceptionMessage(
                requireContext(),
                state.getThrowable()
        );
    }
}
