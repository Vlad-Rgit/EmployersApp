package com.employersapps.employersapp.presentation.group_chat_details.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.employersapps.core.domain.Chat;
import com.employersapps.core.domain.Employer;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.FragmentGroupChatDetailsBinding;
import com.employersapps.employersapp.framework.domain.ParcelableEmployer;
import com.employersapps.employersapp.framework.util.PaddingDecorator;
import com.employersapps.employersapp.framework.util.StreamHelper;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.framework.util.UriHelper;
import com.employersapps.employersapp.presentation.employers_choice_list.ui.EmployersChoiceListFragment;
import com.employersapps.employersapp.presentation.group_chat_details.adapters.EmployersAdapter;
import com.employersapps.employersapp.presentation.group_chat_details.models.ChatData;
import com.employersapps.employersapp.presentation.group_chat_details.models.Result;
import com.employersapps.employersapp.presentation.group_chat_details.models.ResultData;
import com.employersapps.employersapp.presentation.group_chat_details.viewmodel.GroupChatDetailsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupChatDetailsFragment extends Fragment {

    public static final String EXTRA_READ_ONLY = "com.employersapps" +
            ".employersapp.presentation" +
            ".group_chat_details.ui.GroupChatDetailsFragment" +
            ".EXTRA_READ_ONLY";

    public static final String EXTRA_IS_EDIT = "com.employersapps" +
            ".employersapp.presentation" +
            ".group_chat_details.ui.GroupChatDetailsFragment" +
            ".EXTRA_IS_EDIT";

    public static final String EXTRA_CHAT ="com.employersapps" +
            ".employersapp.presentation" +
            ".group_chat_details.ui.GroupChatDetailsFragment" +
            ".EXTRA_CHAT";


    private FragmentGroupChatDetailsBinding binding;
    private EmployersAdapter adapter;
    private GroupChatDetailsViewModel viewModel;
    private String uri = null;
    private boolean isEdit = false;
    private boolean isReadOnly = false;
    private Chat editedChat = null;
    private boolean hasLogo = false;

    private ActivityResultLauncher<String> launcherRequestBrowsePermission;
    private ActivityResultLauncher<String> launcherChooseLogo;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(GroupChatDetailsViewModel.class);


        initActivityLaunchers();
    }


    private void initActivityLaunchers() {

        launcherRequestBrowsePermission =  registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            chooseLogo();
                        }
                    }
                }
        );

        launcherChooseLogo = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result != null) {
                            try {
                                uri = result.toString();

                                byte[] image = StreamHelper.readAllBytes(
                                        requireContext()
                                                .getContentResolver()
                                                .openInputStream(result)
                                );

                                Bitmap bitmap = BitmapFactory.decodeByteArray(
                                        image,
                                        0,
                                        image.length
                                );

                                binding.imgLogo.setImageBitmap(bitmap);
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
        );
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = FragmentGroupChatDetailsBinding.inflate(
                inflater,
                container,
                false
        );



        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMode();
        initToolbar();
        initRecyclerView();
        initButtons();
        observeViewModel();
        observeFragmentResults();
    }

    private void initMode() {
        Bundle args = getArguments();

        if(args == null) {
            initCreate();
        }
        else {

            isReadOnly = args.getBoolean(EXTRA_READ_ONLY, false);
            isEdit = args.getBoolean(EXTRA_IS_EDIT, false);

            if(isReadOnly) {
                initReadOnly(args);
            }
            else if(isEdit) {
                initEdit(args);
            }
        }
    }

    private void initReadOnly(Bundle args) {

        Chat chat = (Chat) args.getSerializable(EXTRA_CHAT);

        if(chat.getLogoPath() == null) {
            Glide.with(binding.imgLogo)
                    .load(R.drawable.chat_logo)
                    .into(binding.imgLogo);
        }
        else {
            Glide.with(binding.imgLogo)
                    .load(Config.API_ADDRESS + "/static/logos/" +
                            chat.getLogoPath())
                    .into(binding.imgLogo);
        }

        binding.btnChooseLogo.setVisibility(View.GONE);
        binding.btnAddEmployers.setVisibility(View.INVISIBLE);
        binding.switchWriteOnlyAdmin.setVisibility(View.GONE);
        binding.tvEmployersHeader.setVisibility(View.VISIBLE);

        binding.edName.setText(chat.getName());
        binding.edName.setEnabled(false);

        int textColor = ContextCompat
                .getColor(
                        requireContext(),
                        R.color.black
                );

        binding.edName.setTextColor(textColor);

        viewModel.addEmployers(chat.getUsers());

        editedChat = chat;
    }

    private void initCreate() {

        binding.imgLogo.setImageResource(
                R.drawable.chat_logo
        );

        binding.toolbar.inflateMenu(R.menu.menu_edit_group_chat);
    }


    private void initEdit(Bundle args) {

        binding.toolbar.inflateMenu(R.menu.menu_edit_group_chat);

        editedChat = (Chat) args.getSerializable(EXTRA_CHAT);

        binding.edName.setText(editedChat.getName());

        if(editedChat.getLogoPath() == null) {
            Glide.with(binding.imgLogo)
                    .load(R.drawable.chat_logo)
                    .into(binding.imgLogo);

            hasLogo = false;
        }
        else {
            Glide.with(binding.imgLogo)
                    .load(Config.API_ADDRESS + "/static/logos/"
                        + editedChat.getLogoPath())
                    .into(binding.imgLogo);

            hasLogo = true;
        }

        binding.switchWriteOnlyAdmin.setChecked(editedChat.isAllowWriteOnlyAdmin());
        viewModel.addEmployers(editedChat.getUsers());
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

                    binding.txtLayoutName.setError(null);


                    String localUri;

                    if(isEdit) {
                        localUri = uri;
                    }
                    else {
                        if (uri == null) {
                            localUri = UriHelper.getUriToDrawable(
                                    requireContext(),
                                    R.drawable.chat_logo
                            ).toString();
                        } else {
                            localUri = uri;
                        }
                    }

                    String name = binding.edName.getText().toString();

                    if(name.trim().isEmpty()) {
                        binding.txtLayoutName.setError(
                                requireContext().getString(
                                        R.string.chat_name_is_required
                                )
                        );
                        return true;
                    }

                    if(viewModel.isEmployersEmpty()) {
                        ToastUtil.showLongToast(
                                requireContext(),
                                R.string.please_choose_at_least_1_employer
                        );
                        return true;
                    }


                    if(isEdit) {

                        ChatData data = new ChatData(
                                editedChat.getId(),
                                name,
                                localUri,
                                binding.switchWriteOnlyAdmin.isChecked()
                        );

                        viewModel.updateGroupChat(data);
                    }
                    else {

                        ChatData data = new ChatData(
                                name,
                                localUri,
                                binding.switchWriteOnlyAdmin.isChecked()
                        );

                        viewModel.postGroupChat(data);
                    }

                    return true;
                }

                return false;
            }
        });
    }



    private void observeViewModel() {
        viewModel.getEmployersLiveData().observe(getViewLifecycleOwner(), new Observer<List<Employer>>() {
            @Override
            public void onChanged(List<Employer> employers) {
                adapter.setItems(employers);
            }
        });

        viewModel.getResultLiveData().observe(getViewLifecycleOwner(), new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                renderResult(result);
            }
        });
    }

    private void renderResult(Result result) {
        if(result instanceof ResultData.Success) {
            Navigation.findNavController(binding.getRoot())
                    .navigateUp();
        }
        else if(result instanceof ResultData.Error) {
            ResultData.Error error = (ResultData.Error) result;

            if(error.getThrowable().getMessage().equals("Forbidden")) {
                binding.txtLayoutName.setError("Chat with this name already exists");
            }
            else {
                ToastUtil.showExceptionMessage(
                        requireContext(),
                        error.getThrowable()
                );
            }
        }
    }

    private void observeFragmentResults() {
        getParentFragmentManager().setFragmentResultListener(
                EmployersChoiceListFragment.KEY_CHOSEN_EMPLOYERS,
                getViewLifecycleOwner(),
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull @NotNull String requestKey,
                                                 @NonNull @NotNull Bundle result) {

                        List<Parcelable> parcelables = result.getParcelableArrayList(
                                EmployersChoiceListFragment.KEY_CHOSEN_EMPLOYERS
                        );

                        List<Employer> employers = new ArrayList<>(parcelables.size());

                        for(Parcelable p : parcelables) {
                            ParcelableEmployer emp = (ParcelableEmployer) p;
                            employers.add(emp.getEmployer());
                        }

                        viewModel.addEmployers(employers);
                    }
                }
        );
    }

    private void initRecyclerView() {

        adapter = new EmployersAdapter(isReadOnly);

        if(isEdit || isReadOnly) {
            adapter.setChatCreatorId(editedChat.getCreatorId());
        }

        adapter.setOnDeleteClickedListener(new EmployersAdapter.OnEmployerClickedListener() {
            @Override
            public void onEmployerClicked(Employer employer) {
                viewModel.removeEmployer(employer);
            }
        });


        RecyclerView rvEmployer =  binding.rvEmployers;

        rvEmployer.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvEmployer.setAdapter(adapter);

        int padding = requireContext()
                .getResources()
                .getDimensionPixelSize(R.dimen.default_padding);

        rvEmployer.addItemDecoration(new PaddingDecorator(
                0, padding, 0, 0
        ));
    }


    private void initButtons() {
        binding.btnAddEmployers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot())
                    .navigate(
                            R.id.action_groupChatDetailsFragment_to_employersChoiceListFragment
                    );
            }
        });

        binding.btnChooseLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChooseLogo();
            }
        });
    }


    private void startChooseLogo() {
        if(ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED) {
            chooseLogo();
        }
        else {
            requestBrowsePermission();
        }
    }

    private void requestBrowsePermission() {
        launcherRequestBrowsePermission.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void chooseLogo() {
        launcherChooseLogo.launch("image/*");
    }
}
