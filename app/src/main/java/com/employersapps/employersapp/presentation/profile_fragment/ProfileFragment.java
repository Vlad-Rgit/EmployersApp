package com.employersapps.employersapp.presentation.profile_fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.network.PostEmployer;
import com.employersapps.core.utils.StateIsNotSupported;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.FragmentEmployerDetailsBinding;
import com.employersapps.employersapp.databinding.FragmentProfileBinding;
import com.employersapps.employersapp.framework.domain.ParcelableEmployer;
import com.employersapps.employersapp.framework.util.StreamHelper;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.EmployerChangedState;
import com.employersapps.employersapp.presentation.employer_details_fragment.state.EmployerDetailsState;
import com.employersapps.employersapp.presentation.employer_details_fragment.viewmodel.EmployerDetailsViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileFragment extends Fragment {
    public static final String KEY_EMPLOYER = "keyEmployer";

    private ProfileViewModel viewModel;
    private FragmentProfileBinding binding;
    private Employer currentEmployer;

    private ActivityResultLauncher<String> launchRequestBrowsePermission;
    private ActivityResultLauncher<String> launchGetPhoto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(ProfileViewModel.class);


        initActivityLaunchers();
    }

    private void initActivityLaunchers() {
        launchRequestBrowsePermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            pickPhoto();
                        }
                    }
                }
        );

        launchGetPhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result != null) {

                            try {


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

                                binding.img.setImageBitmap(bitmap);

                                viewModel.updatePhoto(result);

                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
        );
    }

    private void startPickPhoto() {
        if(ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED) {
            pickPhoto();
        }
        else {
            requestBrowsePermission();
        }
    }

    private void requestBrowsePermission() {
        launchRequestBrowsePermission.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE
        );
    }

    private void pickPhoto() {
        launchGetPhoto.launch("image/*");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observerViewModel();



        binding.btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPickPhoto();
            }
        });

        binding.switchLocationPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.updateStatus(isChecked ? 1 : 2);
            }
        });

        binding.btnChooseStartVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Employer employer = currentEmployer;

                if(employer != null) {
                    LocalDate date;

                    if(employer.getStartVacationDate() == null) {
                        date = LocalDate.now();
                    }
                    else {
                        date = employer.getStartVacationDate();
                    }

                    new DatePickerDialog(
                            requireContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    LocalDate newDate = LocalDate.of(year, month + 1, dayOfMonth);
                                    viewModel.updateEmployer(
                                            new PostEmployer(
                                                    employer.getId(),
                                                    employer.getLastName(),
                                                    employer.getFirstName(),
                                                    employer.getLogin(),
                                                    "",
                                                    employer.getPostId(),
                                                    employer.getRoleId(),
                                                    newDate,
                                                    employer.getEndVacationDate(),
                                                    employer.getVacationComment()
                                            )
                                    );

                                }
                            },
                            date.getYear(),
                            date.getMonthValue() - 1,
                            date.getDayOfMonth()
                    ).show();
                }
            }
        });

        binding.btnChooseEndVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Employer employer = currentEmployer;

                if (employer != null) {
                    LocalDate date;

                    if (employer.getStartVacationDate() == null) {
                        date = LocalDate.now();
                    } else {
                        date = employer.getStartVacationDate();
                    }

                    new DatePickerDialog(
                            requireContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    LocalDate newDate = LocalDate.of(year, month + 1, dayOfMonth);
                                    viewModel.updateEmployer(
                                            new PostEmployer(
                                                    employer.getId(),
                                                    employer.getLastName(),
                                                    employer.getFirstName(),
                                                    employer.getLogin(),
                                                    "",
                                                    employer.getPostId(),
                                                    employer.getRoleId(),
                                                    employer.getStartVacationDate(),
                                                    newDate,
                                                    employer.getVacationComment()
                                            )
                                    );

                                }
                            },
                            date.getYear(),
                            date.getMonthValue() - 1,
                            date.getDayOfMonth()
                    ).show();
                }
            }
        });


        binding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Employer employer = currentEmployer;

                if(employer != null) {

                    Context context = requireContext();

                    LayoutInflater layoutInflater = LayoutInflater.from(context);

                    View dialogContent = layoutInflater.inflate(
                            R.layout.dialog_comment_content,
                            null,
                            false
                    );

                    TextInputEditText edComment = dialogContent.findViewById(R.id.ed_comment);

                    AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                            .setTitle(R.string.vacation_comment)
                            .setView(dialogContent)
                            .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    viewModel.updateEmployer(new PostEmployer(
                                            employer.getId(),
                                            employer.getLastName(),
                                            employer.getFirstName(),
                                            employer.getLogin(),
                                            "",
                                            employer.getPostId(),
                                            employer.getRoleId(),
                                            employer.getStartVacationDate(),
                                            employer.getEndVacationDate(),
                                            edComment.getText().toString()
                                    ));
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Nothing to do
                                }
                            })
                            .show();
                }
            }
        });
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
        else {
            throw new StateIsNotSupported(state.getClass().getName());
        }
    }

    private void renderError(EmployerChangedState.Error state) {
        ToastUtil.showExceptionMessage(requireContext(),
                state.getThrowable());
    }


    private void bindEmployer(Employer employer) {

        currentEmployer = employer;
        binding.tvFirstName.setText(employer.getFirstName());
        binding.tvLastName.setText(employer.getLastName());
        binding.tvPost.setText(employer.getPost().getName());
        binding.tvVacation.setText(employer.getVacationComment());

        DateTimeFormatter format = DateTimeFormatter.ISO_DATE;

        if(employer.getStartVacationDate() != null) {
            binding.tvStartVacationDate.setText(
                    employer.getStartVacationDate().format(format)
            );
        }

        if(employer.getEndVacationDate() != null) {
            binding.tvEndVacationDate.setText(
                    employer.getEndVacationDate().format(format)
            );
        }

        if(employer.isLocationPublic()) {
            binding.tvStatus.setText(employer.getStatus().getName());
            binding.switchLocationPublic.setChecked(true);
        }
        else {
            binding.tvStatus.setText(R.string.status_is_hidden);
            binding.switchLocationPublic.setChecked(false);
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
