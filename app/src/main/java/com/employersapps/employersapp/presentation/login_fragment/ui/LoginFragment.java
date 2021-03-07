package com.employersapps.employersapp.presentation.login_fragment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.employersapps.core.domain.UserLogin;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.FragmentLoginBinding;
import com.employersapps.employersapp.framework.util.KeyboardUtil;
import com.employersapps.employersapp.presentation.login_fragment.intents.LoginIntent;
import com.employersapps.employersapp.presentation.login_fragment.states.ErrorCredentialsState;
import com.employersapps.employersapp.presentation.login_fragment.states.ExceptionState;
import com.employersapps.employersapp.presentation.login_fragment.states.LoadingState;
import com.employersapps.employersapp.presentation.login_fragment.states.LoggedState;
import com.employersapps.employersapp.presentation.login_fragment.states.LoginFragmentState;
import com.employersapps.employersapp.presentation.login_fragment.states.TimeoutServerState;
import com.employersapps.employersapp.presentation.login_fragment.states.UnknownErrorState;
import com.employersapps.employersapp.presentation.login_fragment.viewmodel.LoginFragmentViewModel;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginFragmentViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ViewModelProvider.AndroidViewModelFactory viewModelFactory =
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication());

        viewModel = new ViewModelProvider(
                this,
                viewModelFactory)
                .get(LoginFragmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(
                inflater,
                container,
                false
        );


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initButtons();
        observeViewModel();
    }

    private void initButtons() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = binding.edLogin.getText().toString();
                String password = binding.edPassword.getText().toString();
                UserLogin userLogin = new UserLogin(login, password);
                viewModel.sendIntent(new LoginIntent(userLogin));
            }
        });
    }

    private void observeViewModel() {
        viewModel.getState().observe(getViewLifecycleOwner(), new Observer<LoginFragmentState>() {
            @Override
            public void onChanged(LoginFragmentState loginFragmentState) {
                render(loginFragmentState);
            }
        });
    }


    private void render(LoginFragmentState state) {
        if(state instanceof LoadingState) {
            renderLoadingState();
        }
        else if(state instanceof LoggedState) {
            renderLoggedState((LoggedState) state);
        }
        else if(state instanceof ErrorCredentialsState) {
            renderErrorCredentialsState();
        }
        else if(state instanceof ExceptionState) {
            renderExceptionState((ExceptionState) state);
        }
        else if(state instanceof TimeoutServerState) {
            renderTimeoutServerState();
        }
        else if(state instanceof UnknownErrorState) {
            renderUnknownErrorState((UnknownErrorState)state);
        }
    }

    private void renderTimeoutServerState() {
        cancelLoadingState();
        showToast(getString(R.string.server_timeout));
    }

    private void renderErrorCredentialsState() {
        cancelLoadingState();
        showToast(getString(R.string.error_credentials));
    }

    private void renderUnknownErrorState(UnknownErrorState state) {
        cancelLoadingState();
        showToast(state.getServerResponse().getMessage());
    }

    private void renderExceptionState(ExceptionState state) {
        cancelLoadingState();
        showToast(state.getThrowable().getMessage());
    }

    private void renderLoggedState(LoggedState state) {

        KeyboardUtil.hideKeyboard(binding.getRoot());

        Navigation.findNavController(requireView())
                .navigate(
                        R.id.action_loginFragment_to_mainFragment
                );
    }

    private void renderLoadingState() {
        binding.btnLogin.setVisibility(View.INVISIBLE);
        binding.progressCircular.setVisibility(View.VISIBLE);
    }

    private void cancelLoadingState() {
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.progressCircular.setVisibility(View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }

}
