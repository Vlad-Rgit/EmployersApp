package com.employersapps.employersapp.presentation.chat_fragment.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.MessageAttachment;
import com.employersapps.core.utils.StateIsNotSupported;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.FragmentChatBinding;
import com.employersapps.employersapp.framework.util.ContentHelper;
import com.employersapps.employersapp.framework.util.PaddingDecorator;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.chat_fragment.adapter.ChatAdapter;
import com.employersapps.employersapp.presentation.chat_fragment.intent.AddAttachmentIntent;
import com.employersapps.employersapp.presentation.chat_fragment.intent.AddMessageIntent;
import com.employersapps.employersapp.presentation.chat_fragment.intent.DeleteChatIntent;
import com.employersapps.employersapp.presentation.chat_fragment.intent.RemoveAttachmentIntent;
import com.employersapps.employersapp.presentation.chat_fragment.state.ChatFragmentState;
import com.employersapps.employersapp.presentation.chat_fragment.state.ChatState;
import com.employersapps.employersapp.presentation.chat_fragment.state.DeleteChatState;
import com.employersapps.employersapp.presentation.chat_fragment.state.ListState;
import com.employersapps.employersapp.presentation.chat_fragment.state.MessageAttachmentsState;
import com.employersapps.employersapp.presentation.chat_fragment.state.ReceiverState;
import com.employersapps.employersapp.presentation.chat_fragment.viewmodel.ChatViewModel;
import com.employersapps.employersapp.presentation.group_chat_details.ui.GroupChatDetailsFragment;
import com.employersapps.employersapp.presentation.messages_fragment.adapter.AttachmentsAdapter;

import java.util.Collections;

public class ChatFragment extends Fragment {

    private static final int CODE_REQUEST_READ_PERMISSION = 1;
    private static final int CODE_REQUEST_WRITE_PERMISSION = 3;
    private static final int CODE_REQUEST_PICK_FILE = 2;

    public static final String KEY_CHAT_ID = "keyChatId";

    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private ChatAdapter chatAdapter;
    private boolean receivedChat = false;

    private MessageAttachment pendingDownload;

    private AttachmentsAdapter attachmentsAdapter;

    private Chat chat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()))
                .get(ChatViewModel.class);

        Bundle args = getArguments();

        long chatId = args.getLong(KEY_CHAT_ID, -1);

        viewModel.init(chatId);

        chatAdapter = new ChatAdapter();
        chatAdapter.setCurrentUserId(viewModel.getCurrentUserId());
        chatAdapter.setOnAttachmentClickedListener(this::startDownloadAttachment);

        attachmentsAdapter = new AttachmentsAdapter();

        attachmentsAdapter.setOnRemoveClickedListener(new AttachmentsAdapter.OnRemoveClickedListener() {
            @Override
            public void onRemoveClicked(MessageAttachment messageAttachment) {
                viewModel.sendIntent(new RemoveAttachmentIntent(messageAttachment));
            }
        });
    }

    private void startDownloadAttachment(MessageAttachment messageAttachment) {
        if(ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED) {
            pendingDownload = messageAttachment;
            requestWritePermission();
        }
        else {
            downloadAttachment(messageAttachment);
        }
    }

    private void downloadAttachment(MessageAttachment messageAttachment) {
        Uri uri = Uri.parse(Config.API_ADDRESS +
                "/static/files/messages/" +
                messageAttachment.getName());

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(messageAttachment.getShortName());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, messageAttachment.getShortName());

        DownloadManager downloadManager = (DownloadManager)
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE);

        downloadManager.enqueue(request);

        ToastUtil.showLongToast(
                requireContext(),
                R.string.download_started
        );

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(
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
        initRecyclerView();
        initAttachmentsRecyclerView();
        initButtons();
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.refreshChatInfo();
    }

    private void initButtons() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = binding.edText.getText().toString();

                if(!text.isEmpty() || viewModel.attachmentsCount() > 0) {
                    viewModel.sendIntent(new AddMessageIntent(text));
                    attachmentsAdapter.setItems(Collections.emptyList());
                    binding.rvAttachments.setVisibility(View.GONE);
                    binding.edText.setText("");
                    binding.rvMessages.smoothScrollToPosition(chatAdapter.getItemCount());
                }
            }
        });

        binding.btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickFile();
            }
        });
    }

    private void requestWritePermission() {
        requestPermissions(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CODE_REQUEST_WRITE_PERMISSION
        );
    }

    private void requestReadPermission() {
        requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                CODE_REQUEST_READ_PERMISSION
        );
    }

    private void startPickFile() {
        if(ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED) {
            requestReadPermission();
        }
        else {
            pickFile();
        }
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, CODE_REQUEST_PICK_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK || data == null) {
            return;
        }

        if(requestCode == CODE_REQUEST_PICK_FILE) {
            if(data.getClipData() == null) {

                MessageAttachment messageAttachment = new MessageAttachment(
                        data.getDataString(),
                        ContentHelper.getFileName(requireContext(), data.getData()),
                        ContentHelper.getFileSize(requireContext(), data.getData())
                );

                viewModel.sendIntent(new AddAttachmentIntent(messageAttachment));
            }
            else {
                ClipData clipData = data.getClipData();

                for(int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();

                    MessageAttachment messageAttachment = new MessageAttachment(
                            uri.toString(),
                            ContentHelper.getFileName(requireContext(), uri),
                            ContentHelper.getFileSize(requireContext(), uri)
                    );

                    viewModel.sendIntent(new AddAttachmentIntent(messageAttachment));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == CODE_REQUEST_READ_PERMISSION) {
                pickFile();
            }
            else if(requestCode == CODE_REQUEST_WRITE_PERMISSION) {
                downloadAttachment(pendingDownload);
            }
        }
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot())
                        .navigateUp();
            }
        });

        if(chat != null &&
            !chat.isPrivate()) {
            initToolbarMenu();
        }
    }

    private void initAttachmentsRecyclerView() {
        RecyclerView rvAttachments = binding.rvAttachments;

        rvAttachments.setHasFixedSize(true);

        rvAttachments.setLayoutManager(new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        rvAttachments.setAdapter(attachmentsAdapter);

        int padding = requireContext()
                .getResources()
                .getDimensionPixelSize(R.dimen.default_padding) / 2;

        rvAttachments.addItemDecoration(new PaddingDecorator(0, padding, 0, 0));

    }

    private void initRecyclerView() {

        RecyclerView rvMessages = binding.rvMessages;

        rvMessages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(chatAdapter);

        rvMessages.setItemAnimator(null);

        rvMessages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {

            }
        });

        int padding = requireContext()
                .getResources()
                .getDimensionPixelSize(R.dimen.default_padding) / 2;

        rvMessages.addItemDecoration(new PaddingDecorator(0, padding, 0, 0));
    }

    private void observeViewModel() {

        viewModel.getChatState().observe(getViewLifecycleOwner(), new Observer<ChatFragmentState>() {
            @Override
            public void onChanged(ChatFragmentState state) {
                render(state);
            }
        });

        viewModel.getState().observe(getViewLifecycleOwner(), new Observer<ChatFragmentState>() {
            @Override
            public void onChanged(ChatFragmentState state) {
                render(state);
            }
        });

        viewModel.getReceiverState().observe(getViewLifecycleOwner(), new Observer<ReceiverState>() {
            @Override
            public void onChanged(ReceiverState state) {
                render(state);
            }
        });
    }

    private void render(ChatFragmentState state) {
        if(state instanceof ListState) {
            renderMessages((ListState)state);
        }
        else if (state instanceof ReceiverState) {
            ReceiverState receiverState = (ReceiverState) state;
            renderReceiver(receiverState.getReceiver());
        }
        else if(state instanceof MessageAttachmentsState) {
            renderMessageAttachmentsState((MessageAttachmentsState)state);
        }
        else if(state instanceof ChatState) {
            renderChatState((ChatState) state);
        }
        else if(state instanceof DeleteChatState.Success) {
            Navigation.findNavController(
                    binding.getRoot()
            ).navigateUp();
        }
        else if(state instanceof DeleteChatState.Error) {
            DeleteChatState.Error error = (DeleteChatState.Error)state;
            ToastUtil.showExceptionMessage(
                    requireContext(),
                    error.getThrowable()
            );
        }
        else {
            throw new StateIsNotSupported(state.getClass().getName());
        }
    }

    private void renderChatState(ChatState state) {

        chat = state.getChat();

        chatAdapter.setPrivateChat(chat.isPrivate());

        binding.tvReceiverName.setText(chat.getName());
        binding.tvReceiverStatus.setVisibility(View.GONE);

        if(chat.getLogoPath() == null) {

            Glide.with(binding.imgReceiver)
                    .load(R.drawable.chat_logo)
                    .into(binding.imgReceiver);
        }
        else {

            String url = Config.API_ADDRESS + "/static/logos/"
                    + chat.getLogoPath();

            Glide.with(binding.imgReceiver)
                    .load(url)
                    .into(binding.imgReceiver);
        }

        if(!receivedChat) {
            initToolbarMenu();
        }


        if(!viewModel.isUserAdmin()) {

            if(chat.isAllowWriteOnlyAdmin()) {
                binding.btnSend.setVisibility(View.GONE);
                binding.edText.setVisibility(View.GONE);
                binding.btnAttach.setVisibility(View.GONE);
            }

            MenuItem editMenu = binding.toolbar.getMenu().findItem(
                    R.id.menu_edit_chat
            );

            MenuItem deleteMenu = binding.toolbar.getMenu().findItem(
                    R.id.menu_delete_chat
            );

            deleteMenu.setVisible(false);
            editMenu.setVisible(false);
        }

        receivedChat = true;
    }

    private void initToolbarMenu() {
        binding.toolbar.inflateMenu(R.menu.chat_menu);

        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_show_info: showChatInfo();
                        return true;
                    case R.id.menu_edit_chat: editChat();
                        return true;
                    case R.id.menu_delete_chat: deleteChat();
                }
                return false;
            }
        });
    }

    private void deleteChat() {
        viewModel.sendIntent(new DeleteChatIntent());
    }

    private void showChatInfo() {
        Bundle args = new Bundle();
        args.putBoolean(GroupChatDetailsFragment.EXTRA_READ_ONLY, true);
        args.putSerializable(GroupChatDetailsFragment.EXTRA_CHAT, chat);
        Navigation.findNavController(binding.getRoot())
                .navigate(
                        R.id.action_chatFragment_to_groupChatDetailsFragment,
                        args
                );
    }

    private void editChat() {
        Bundle args = new Bundle();

        args.putBoolean(GroupChatDetailsFragment.EXTRA_IS_EDIT, true);
        args.putSerializable(GroupChatDetailsFragment.EXTRA_CHAT, chat);

        Navigation.findNavController(
                binding.getRoot()
        ).navigate(
                R.id.action_chatFragment_to_groupChatDetailsFragment,
                args
        );
    }

    private void renderMessageAttachmentsState(MessageAttachmentsState state) {
        if(state.getAttachments().size() > 0) {
            binding.rvAttachments.setVisibility(View.VISIBLE);
        }
        else {
            binding.rvAttachments.setVisibility(View.GONE);
        }
        attachmentsAdapter.setItems(state.getAttachments());
    }

    private void renderMessages(ListState state) {
        if(state.getMessages() != null) {

            chatAdapter.setItems(state.getMessages());

            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    binding.rvMessages.getLayoutManager();

            if(layoutManager.findLastCompletelyVisibleItemPosition() == state.getMessages().size() - 2) {
                binding.rvMessages.smoothScrollToPosition(state.getMessages().size() - 1);
            }
        }


        if(state.getReceiver() != null) {
            renderReceiver(state.getReceiver());
        }
    }

    private void renderReceiver(Employer employer) {

        binding.tvReceiverName.setText(employer.getShortFullName());

        if(employer.isLocationPublic()) {
            binding.tvReceiverStatus.setText(employer.getStatus().getName());
        }
        else {
            binding.tvReceiverStatus.setText(R.string.status_is_hidden);
        }

        String imgUrl = Config.API_ADDRESS + "/static/employers/"
                + employer.getPhotoPath();

        Glide.with(binding.imgReceiver)
                .load(imgUrl)
                .error(R.drawable.no_image)
                .into(binding.imgReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvMessages.setAdapter(null);
    }
}
