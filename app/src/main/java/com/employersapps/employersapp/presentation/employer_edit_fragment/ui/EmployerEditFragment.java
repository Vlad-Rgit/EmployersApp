package com.employersapps.employersapp.presentation.employer_edit_fragment.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.Post;
import com.employersapps.core.domain.Role;
import com.employersapps.core.domain.network.PostEmployer;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.FragmentEmployerEditBinding;
import com.employersapps.employersapp.framework.domain.ParcelableEmployer;
import com.employersapps.employersapp.framework.util.StreamHelper;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.employer_edit_fragment.intent.AddEmployerIntent;
import com.employersapps.employersapp.presentation.employer_edit_fragment.intent.UpdateEmployerIntent;
import com.employersapps.employersapp.presentation.employer_edit_fragment.state.EmployerEditState;
import com.employersapps.employersapp.presentation.employer_edit_fragment.state.SaveState;
import com.employersapps.employersapp.presentation.employer_edit_fragment.viewmodel.EmployerEditViewModel;

import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import java.util.List;

public class EmployerEditFragment extends Fragment {

    public static final String EXTRA_EDITABLE_EMPLOYER = "com.employersapps.employersapp" +
            ".presentation.employer_edit_fragment.ui.EditableEmployer";

    private static final int REQUEST_READ_PERMISSION_CODE = 1;
    private static final int REQUEST_PICK_PHOTO = 2;

    private FragmentEmployerEditBinding binding;
    private EmployerEditViewModel viewModel;
    private String photoUri;
    private boolean isEdit = false;
    private PostEmployer editableEmployer;
    private String photoPath;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            Parcelable parcelable = getArguments().getParcelable(
                    EXTRA_EDITABLE_EMPLOYER
            );

            if(parcelable != null) {
                ParcelableEmployer parcelableEmployer = (ParcelableEmployer)
                        parcelable;
                Employer employer = parcelableEmployer.getEmployer();
                editableEmployer = new PostEmployer(
                        employer.getId(),
                        employer.getLastName(),
                        employer.getFirstName(),
                        employer.getLogin(),
                        "",
                        employer.getPostId(),
                        employer.getRoleId(),
                        employer.getStartVacationDate(),
                        employer.getEndVacationDate(),
                        employer.getVacationComment()
                );
                photoPath = employer.getPhotoPath();
                isEdit = true;
            }
        }


        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(EmployerEditViewModel.class);

    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentEmployerEditBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view,
                              @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTextLayouts();
        initToolbar();
        initButtons();
        initImage();
        observeViewModel();
    }

    private void initImage() {
        if(isEdit) {
            if(photoPath != null && !photoPath.equals("[null]")) {
                Glide.with(binding.imageView)
                        .load(Config.API_ADDRESS + "/static/employers/" + photoPath)
                        .into(binding.imageView);
            }
        }
    }

    private void render(EmployerEditState state) {
        if(state instanceof SaveState.Succes) {
            ToastUtil.showLongToast(
                    requireContext(),
                    "Changes has been saved"
            );
            Navigation.findNavController(binding.getRoot())
                    .popBackStack();
        }
        else if(state instanceof SaveState.Error) {
            SaveState.Error error = (SaveState.Error) state;

            if(error.getThrowable().getMessage().equals("Forbidden")) {
                binding.txtLayoutLogin.setError(
                        "Employer with this login already exists"
                );
                return;
            }

            ToastUtil.showExceptionMessage(
                    requireContext(),
                    error.getThrowable()
            );
        }
    }

    private void observeViewModel() {
        viewModel.getPostsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                initSpinner(binding.spinnerPosts, posts);
                if(isEdit) {
                    binding.spinnerPosts.setSelection(editableEmployer.postId - 1);
                }
            }
        });

        viewModel.getRolesLiveData().observe(getViewLifecycleOwner(), new Observer<List<Role>>() {
            @Override
            public void onChanged(List<Role> roles) {
                initSpinner(binding.spinnerRoles, roles);
                if(isEdit) {
                    binding.spinnerRoles.setSelection(editableEmployer.roleId - 1);
                }
            }
        });

        viewModel.getState().observe(getViewLifecycleOwner(), new Observer<EmployerEditState>() {
            @Override
            public void onChanged(EmployerEditState state) {
                render(state);
            }
        });
    }

    private void initButtons() {
        binding.btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRequestPhoto();
            }
        });
    }



    private void startRequestPhoto() {
        if(ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED) {
            requestPhoto();
        }
        else {
            requestReadPermission();
        }
    }

    private void requestReadPermission() {
        requestPermissions(
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_PERMISSION_CODE
        );
    }

    private <T> void initSpinner(Spinner spinner, List<T> items) {

        ArrayAdapter<T> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    private void requestPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PICK_PHOTO &&
                resultCode == Activity.RESULT_OK &&
                data != null &&
                data.getData() != null) {

            try {
                photoUri = data.getDataString();
                binding.imageView.setImageURI(data.getData());
            }
            catch (Exception ex) {
                ToastUtil.showExceptionMessage(
                        requireContext(),
                        ex
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull @NotNull String[] permissions,
                                           @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_READ_PERMISSION_CODE &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestPhoto();
        }
    }

    private void initTextLayouts() {

        if(isEdit) {
            binding.edFirstName.setText(editableEmployer.firstName);
            binding.edLastName.setText(editableEmployer.lastName);
            binding.edLogin.setText(editableEmployer.login);
        }


        binding.edFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isWhiteSpace(s.toString())) {
                    binding.txtLayoutFirstName.setError(null);
                }
            }
        });

        binding.edLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isWhiteSpace(s.toString())) {
                    binding.txtLayoutLastName.setError(null);
                }
            }
        });

        binding.edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isWhiteSpace(s.toString())) {
                    binding.txtLayoutPassword.setError(null);
                }
            }
        });

        binding.edLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isWhiteSpace(s.toString())) {
                    binding.txtLayoutLogin.setError(null);
                }
            }
        });

        binding.edPasswordRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = binding.edPassword.getText().toString();
                if(password.equals(s.toString())) {
                    binding.txtLayoutPasswordRepeat.setError(null);
                }
            }
        });
    }

    private void initToolbar() {
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v)
                        .popBackStack();
            }
        });

        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_accept) {

                    accept();

                    return true;
                }

                return false;
            }
        });
    }


    private void accept() {

        Post post = (Post) binding.spinnerPosts.getSelectedItem();
        Role role = (Role) binding.spinnerRoles.getSelectedItem();


        PostEmployer postEmployer;

        if(isEdit) {
            postEmployer = new PostEmployer(
                    editableEmployer.id,
                    binding.edLastName.getText().toString(),
                    binding.edFirstName.getText().toString(),
                    binding.edLogin.getText().toString(),
                    binding.edPassword.getText().toString(),
                    post.getId(),
                    role.id,
                    editableEmployer.startVacationDate,
                    editableEmployer.endVacationDate,
                    editableEmployer.vacationComment
            );
        }
        else {
            postEmployer = new PostEmployer(
                    binding.edLastName.getText().toString(),
                    binding.edFirstName.getText().toString(),
                    binding.edLogin.getText().toString(),
                    binding.edPassword.getText().toString(),
                    post.getId(),
                    role.id,
                    null,
                    null,
                    null
            );
        }

        boolean isValid = true;

        binding.txtLayoutLogin.setError(null);

        if(isWhiteSpace(postEmployer.lastName)) {
            binding.txtLayoutLastName.setError("Name is required");
            isValid = false;
        }

        if(isWhiteSpace(postEmployer.firstName)) {
            binding.txtLayoutFirstName.setError("Last name is required");
            isValid = false;
        }

        if(isWhiteSpace(postEmployer.login)) {
            binding.txtLayoutLogin.setError("Login is required");
            isValid = false;
        }

        if(isWhiteSpace(postEmployer.password) && !isEdit) {
            binding.txtLayoutPassword.setError("Password is required");
            isValid = false;
        }

        String repeatPassword = binding.edPasswordRepeat.getText().toString();

        if(!postEmployer.password.equals(repeatPassword)) {
            binding.txtLayoutPasswordRepeat.setError("Passwords are not match");
            isValid = false;
        }


        if(isValid) {
            if(isEdit) {
                viewModel.sendIntent(new UpdateEmployerIntent(postEmployer, photoUri));
            }
            else {
                viewModel.sendIntent(new AddEmployerIntent(postEmployer, photoUri));
            }

        }
    }

    private boolean isWhiteSpace(String s) {
        return s.trim().isEmpty();
    }
}
