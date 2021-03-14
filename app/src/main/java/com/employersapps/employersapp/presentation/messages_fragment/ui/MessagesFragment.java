package com.employersapps.employersapp.presentation.messages_fragment.ui;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.employersapps.core.domain.LastMessageChat;
import com.employersapps.core.domain.Message;
import com.employersapps.core.utils.StateIsNotSupported;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.FragmentMessagesBinding;
import com.employersapps.employersapp.framework.util.PaddingDecorator;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.chat_fragment.ui.ChatFragment;
import com.employersapps.employersapp.presentation.messages_fragment.adapter.MessagesAdapter;
import com.employersapps.employersapp.presentation.messages_fragment.intent.RefreshIntent;
import com.employersapps.employersapp.presentation.messages_fragment.state.ListState;
import com.employersapps.employersapp.presentation.messages_fragment.state.MessagesFragmentState;
import com.employersapps.employersapp.presentation.messages_fragment.viewmodel.MessagesViewModel;

import java.nio.file.Path;
import java.util.List;

public class MessagesFragment extends Fragment {

    private FragmentMessagesBinding binding;
    private MessagesViewModel viewModel;
    private MessagesAdapter messagesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()))
                .get(MessagesViewModel.class);


        messagesAdapter = new MessagesAdapter();
        messagesAdapter.setCurrentUserId(viewModel.getCurrentUserId());

        messagesAdapter.setOnMessageClickedListener(this::goToChat);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentMessagesBinding.inflate(
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
        initSwipeRefresh();
        initRecyclerView();
        observeViewModel();
    }

    private void initToolbar() {
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId() == R.id.menu_marked_messages) {

                    Navigation.findNavController(
                            requireActivity(),
                            R.id.nav_host
                    ).navigate(
                            R.id.action_mainFragment_to_markedMessagesFragment);

                    return true;
                }

                return false;
            }
        });
    }

    private void initButtons() {
        if(viewModel.isUserAdmin()) {
            binding.btnAddGroupChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(
                            requireActivity(),
                            R.id.nav_host
                    ).navigate(
                            R.id.action_mainFragment_to_groupChatDetailsFragment);
                }
            });
        }
        else {
            binding.btnAddGroupChat.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.sendIntent(new RefreshIntent());
    }

    private void initSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.sendIntent(new RefreshIntent());
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView rvMessages = binding.rvMessages;

        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMessages.setAdapter(messagesAdapter);

        int padding = requireContext()
                .getResources()
                .getDimensionPixelSize(R.dimen.default_padding) / 2;

        rvMessages.addItemDecoration(new PaddingDecorator(0, padding, 0, 0));
    }

    private void goToChat(LastMessageChat message) {

        Bundle args = new Bundle();

        args.putLong(ChatFragment.KEY_CHAT_ID, message.getId());

        Navigation.findNavController(
                requireActivity(),
                R.id.nav_host
        ).navigate(
                R.id.action_mainFragment_to_chatFragment,
                args
        );

    }

    private void observeViewModel() {
        viewModel.getState().observe(getViewLifecycleOwner(), new Observer<MessagesFragmentState>() {
            @Override
            public void onChanged(MessagesFragmentState state) {
                render(state);
            }
        });
    }

    private void render(MessagesFragmentState state) {
        if(state instanceof ListState.Success) {
            renderListState((ListState.Success)state);
        }
        else if(state instanceof ListState.Error) {
            renderError((ListState.Error)state);
        }
        else {
            throw new StateIsNotSupported(state.getClass().getName());
        }
    }

    private void renderListState(ListState.Success success) {
        binding.swipeRefresh.setRefreshing(false);
        messagesAdapter.setItems(success.getResult());
    }

    private void renderError(ListState.Error error) {
        binding.swipeRefresh.setRefreshing(false);
        ToastUtil.showExceptionMessage(
                requireContext(),
                error.getThrowable()
        );
    }
}
