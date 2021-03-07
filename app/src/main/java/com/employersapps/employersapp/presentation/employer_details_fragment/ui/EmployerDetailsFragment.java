package com.employersapps.employersapp.presentation.employer_details_fragment.ui;

import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.employersapps.core.data.EmployerChangesDataSource;
import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.state.ErrorState;
import com.employersapps.core.utils.StateIsNotSupported;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.FragmentEmployerDetailsBinding;
import com.employersapps.employersapp.databinding.FragmentEmployersBinding;
import com.employersapps.employersapp.framework.domain.ParcelableEmployer;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.chat_fragment.ui.ChatFragment;
import com.employersapps.employersapp.presentation.employer_details_fragment.intent.CreateChatIntent;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.ChatCreateState;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.EmployerChangedState;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.EmployerDetailsState;
import com.employersapps.employersapp.presentation.employer_details_fragment.viewmodel.EmployerDetailsViewModel;

import java.time.format.DateTimeFormatter;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class EmployerDetailsFragment extends Fragment {

    public static final String KEY_EMPLOYER = "keyEmployer";

    private EmployerDetailsViewModel viewModel;
    private FragmentEmployerDetailsBinding binding;
    private Employer employer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(EmployerDetailsViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentEmployerDetailsBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle args = getArguments();

        if(args == null) {
            throw new IllegalArgumentException("Employer arg is necessary");
        }
        else {
            ParcelableEmployer parcelable = args.getParcelable(KEY_EMPLOYER);
            if(parcelable == null) {
                throw new IllegalArgumentException("Employer arg is necessary");
            }
            else {
                employer = parcelable.getEmployer();
                viewModel.setEmployer(parcelable.getEmployer());
                bindEmployer(parcelable.getEmployer());
            }
        }

        observerViewModel();
        initToolbar();
    }


    private void observerViewModel() {
        viewModel.getState().observe(getViewLifecycleOwner(), new Observer<EmployerDetailsState>() {
            @Override
            public void onChanged(EmployerDetailsState state) {
                render(state);
            }
        });
    }

    private void render(EmployerDetailsState state) {
        if(state instanceof EmployerChangedState.Success) {
            EmployerChangedState.Success success = (EmployerChangedState.Success)
                state;

            bindEmployer(success.getResult());
        }
        else if (state instanceof EmployerChangedState.Error) {
            renderError((EmployerChangedState.Error)state);
        }
        else if (state instanceof ChatCreateState.Error) {
            renderError((ErrorState)state);
        }
        else if (state instanceof ChatCreateState.Success) {
            handleChatCreateSuccess((ChatCreateState.Success)state);
        }
        else {
            throw new StateIsNotSupported(state.getClass().getName());
        }
    }

    private void handleChatCreateSuccess(ChatCreateState.Success state) {
        Bundle args = new Bundle();
        args.putLong(ChatFragment.KEY_CHAT_ID, state.getResult().getId());
        Navigation.findNavController(
                requireActivity(),
                R.id.nav_host
        ).navigate(
                R.id.action_employerDetailsFragment_to_chatFragment,
                args
        );
    }

    private void renderError(ErrorState state) {
        ToastUtil.showExceptionMessage(requireContext(),
                state.getThrowable());
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

        if(employer.getId() == viewModel.getCurrentUserId()) {
            binding.toolbar.getMenu().removeItem(R.id.menu_chat);
        }
        else {
            binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.menu_chat) {

                        viewModel.sendIntent(new CreateChatIntent());

                        return true;
                    }

                    return false;
                }
            });
        }
    }

    private void bindEmployer(Employer employer) {
        binding.tvFirstName.setText(employer.getFirstName());
        binding.tvLastName.setText(employer.getLastName());
        binding.tvPost.setText(employer.getPost().getName());

        DateTimeFormatter format = DateTimeFormatter.ISO_DATE;

        if(employer.getStartVacationDate() != null) {
            binding.tvStartDate.setText(
                    employer.getStartVacationDate().format(
                            format
                    )
            );
        }

        if(employer.getEndVacationDate() != null) {
            binding.tvEndDate.setText(
                    employer.getEndVacationDate().format(
                            format
                    )
            );
        }

        if(employer.getVacationComment() != null) {
            binding.tvVacationComment.setText(employer.getVacationComment());
        }

        if(employer.isLocationPublic()) {
            binding.tvStatus.setText(employer.getStatus().getName());
        }
        else {
            binding.tvStatus.setText(R.string.status_is_hidden);
        }

        int cornerRadius = requireContext().getResources()
                .getDimensionPixelSize(R.dimen.corner_radius);

        Glide.with(binding.img)
                .load(Config.API_ADDRESS + "/static/employers/" + employer.getPhotoPath())
                .error(R.drawable.no_image)
                .transform(new RoundedCornersTransformation(cornerRadius, 0))
                .into(binding.img);
    }
}
