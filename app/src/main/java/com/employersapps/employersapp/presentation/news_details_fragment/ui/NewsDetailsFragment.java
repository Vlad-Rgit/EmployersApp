package com.employersapps.employersapp.presentation.news_details_fragment.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Bundle;
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

import com.employersapps.core.domain.News;
import com.employersapps.core.domain.NewsFile;
import com.employersapps.core.domain.NewsImage;
import com.employersapps.core.domain.network.NewsFileNetwork;
import com.employersapps.core.domain.network.NewsImageNetwork;
import com.employersapps.core.utils.StateIsNotSupported;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.FragmentNewsDetailsBinding;
import com.employersapps.employersapp.framework.util.ContentHelper;
import com.employersapps.employersapp.framework.util.FragmentResultHelper;
import com.employersapps.employersapp.framework.util.PaddingDecorator;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.news_details_fragment.adapter.NewsFilesAdapter;
import com.employersapps.employersapp.presentation.news_details_fragment.adapter.NewsImageAdapter;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddMultipleImageIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddMultipleNewsFilesIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddNewsFileIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddNewsImageIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddNewsIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.RemoveNewsFileIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.RemoveNewsImageIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.UpdateNewsIntent;
import com.employersapps.employersapp.presentation.news_details_fragment.state.ExceptionState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsAddedState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsDetailsState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsFilesState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.NewsImagesListState;
import com.employersapps.employersapp.presentation.news_details_fragment.state.SendingState;
import com.employersapps.employersapp.presentation.news_details_fragment.viewmodel.NewsDetailsViewModel;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewsDetailsFragment extends Fragment {

    private static final int CODE_REQUEST_READ_PERMISSION_FOR_PHOTO = 1;
    private static final int CODE_REQUEST_READ_PERMISSION_FOR_FILE = 2;
    private static final int CODE_REQUEST_PICK_IMAGE = 3;
    private static final int CODE_REQUEST_PICK_FILE = 4;

    public static final String KEY_IS_SUCCESS_ADDED = "keyIsSuccessAdded";

    public static final String EXTRA_EDITABLE_NEWS = "com.employersapps.employersapp" +
            ".presentation.news_details_fragment.ui" +
            ".EXTRA_EDITABLE_NEWS";


    private boolean isEdit = false;
    private News editableNews;

    private FragmentNewsDetailsBinding binding;
    private NewsDetailsViewModel viewModel;
    private NewsImageAdapter newsImageAdapter;
    private NewsFilesAdapter newsFilesAdapter;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle args = getArguments();

        if(args != null) {
            Serializable serializable = args.getSerializable(
                    EXTRA_EDITABLE_NEWS
            );
            if(serializable != null) {
                editableNews = (News)serializable;
                isEdit = true;
            }
        }

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(NewsDetailsViewModel.class);

        newsImageAdapter = new NewsImageAdapter();

        newsImageAdapter.setOnRemoveImageCallback(new NewsImageAdapter.OnRemoveImageCallback() {
            @Override
            public void onRemoveImage(NewsImage newsImage) {
                viewModel.sendIntent(new RemoveNewsImageIntent(newsImage));
            }
        });

        newsFilesAdapter = new NewsFilesAdapter();

        newsFilesAdapter.setOnRemoveClickedListener(this::removeNewsFile);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentNewsDetailsBinding.inflate(
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
        initFilesRecyclerView();
        initImagesRecyclerView();
        initEditableNews();
        observeViewModel();
    }

    private void initEditableNews() {
        if(isEdit) {

            List<NewsImage> newsImages = new ArrayList<>(editableNews
                    .getNewsPhotos().size());

            for (NewsImageNetwork newsImageNetwork : editableNews.getNewsPhotos()) {
                newsImages.add(new NewsImage(newsImageNetwork.getName(), true));
            }

            viewModel.sendIntent(new AddMultipleImageIntent(newsImages));

            List<NewsFile> newsFiles = new ArrayList<>(editableNews
                    .getNewsFiles().size());

            for (NewsFileNetwork newsFileNetwork : editableNews.getNewsFiles()) {
                newsFiles.add(new NewsFile(newsFileNetwork.getName(),
                        newsFileNetwork.getName(),
                        true));
            }

            viewModel.sendIntent(new AddMultipleNewsFilesIntent(newsFiles));

            binding.edTitle.setText(editableNews.getTitle());
            binding.edText.setText(editableNews.getText());
        }
    }

    private void initFilesRecyclerView() {
        RecyclerView rvFiles = binding.rvFiles;

        rvFiles.setHasFixedSize(true);

        rvFiles.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        rvFiles.setAdapter(newsFilesAdapter);

        int margin = requireContext()
                .getResources()
                .getDimensionPixelSize(R.dimen.default_horizontal_margin) / 2;

        rvFiles.addItemDecoration(new PaddingDecorator(0, 0, margin, 0));
    }

    private void removeNewsFile(NewsFile newsFile) {
        viewModel.sendIntent(new RemoveNewsFileIntent(newsFile));
    }

    private void initImagesRecyclerView() {
        RecyclerView rvImages = binding.rvImages;

        rvImages.setHasFixedSize(true);
        rvImages.setLayoutManager(new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        ));
        rvImages.setAdapter(newsImageAdapter);

        int margin = (int)requireContext().getResources()
                .getDimension(R.dimen.default_horizontal_margin) / 2;

        rvImages.addItemDecoration(new PaddingDecorator(0, 0, margin, 0));
    }



    private void initToolbar() {

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot())
                        .navigateUp();
            }
        });

        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.menu_send:
                        processSendNewsIntent();
                        return true;
                    case R.id.menu_attach_photo:
                        proccessAttachFile(CODE_REQUEST_PICK_IMAGE);
                        return true;
                    case R.id.menu_attach_file:
                        proccessAttachFile(CODE_REQUEST_PICK_FILE);
                }
                return false;
            }
        });

        if(isEdit) {
            binding.toolbar.setTitle(R.string.edit_news);
        }
        else {
            binding.toolbar.setTitle(R.string.add_news);
        }

    }

    private void proccessAttachFile(int requestCode) {
        if(ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if(requestCode == CODE_REQUEST_PICK_IMAGE) {
                startAttachImage();
            }
            else if(requestCode == CODE_REQUEST_PICK_FILE) {
                startAttachFile();
            }
        }
        else {
            requestReadPermission(requestCode == CODE_REQUEST_PICK_IMAGE ?
                    CODE_REQUEST_READ_PERMISSION_FOR_PHOTO :
                    CODE_REQUEST_READ_PERMISSION_FOR_FILE);
        }
    }

    private void startAttachFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, CODE_REQUEST_PICK_FILE);
    }

    private void requestReadPermission(int requestCode) {
        requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                requestCode
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(requestCode == CODE_REQUEST_READ_PERMISSION_FOR_PHOTO) {
                startAttachImage();
            }
            else if(requestCode == CODE_REQUEST_READ_PERMISSION_FOR_FILE) {
                startAttachFile();
            }
        }
    }

    private void startAttachImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, CODE_REQUEST_PICK_IMAGE);
    }

    private void observeViewModel() {
        viewModel.getStateLiveData().observe(getViewLifecycleOwner(), new Observer<NewsDetailsState>() {
            @Override
            public void onChanged(NewsDetailsState newsDetailsState) {
                render(newsDetailsState);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == CODE_REQUEST_PICK_IMAGE) {
            if(data != null) {
                if(data.getClipData() == null) {

                    if(viewModel.imagesCount() + 1 > 5) {
                        ToastUtil.showLongToast(
                                requireContext(),
                                R.string.max_images_5
                        );
                        return;
                    }

                    NewsImage newsImage = new NewsImage(data.getDataString());
                    viewModel.sendIntent(new AddNewsImageIntent(newsImage));
                }
                else {
                    ClipData clipData = data.getClipData();

                    if(viewModel.imagesCount() + clipData.getItemCount() > 5) {
                        ToastUtil.showLongToast(
                                requireContext(),
                                R.string.max_images_5
                        );
                        return;
                    }

                    List<NewsImage> newsImages = new ArrayList<>(clipData.getItemCount());

                    for(int i = 0; i < clipData.getItemCount(); i++) {

                        NewsImage newsImage = new NewsImage(
                                clipData.getItemAt(i).getUri().toString()
                        );

                        newsImages.add(newsImage);
                    }

                    viewModel.sendIntent(new AddMultipleImageIntent(newsImages));
                }
            }
        }
        else if(requestCode == CODE_REQUEST_PICK_FILE && data != null) {
            if(data.getClipData() == null) {
                Uri uri = data.getData();
                if(!isFileLessSize(uri)) {
                    ToastUtil.showLongToast(
                            requireContext(),
                            R.string.max_file_size_20mb
                    );
                    return;
                }
                NewsFile newsFile = new NewsFile(
                        ContentHelper.getFileName(requireContext(), uri),
                        uri.toString()
                );
                viewModel.sendIntent(new AddNewsFileIntent(newsFile));
            }
            else {
                ClipData clipData = data.getClipData();
                List<NewsFile> files = new ArrayList<>(clipData.getItemCount());
                for(int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();

                    if(!isFileLessSize(uri)) {
                        ToastUtil.showLongToast(
                                requireContext(),
                                R.string.max_file_size_20mb
                        );
                        return;
                    }

                    NewsFile newsFile = new NewsFile(
                            ContentHelper.getFileName(requireContext(), uri),
                            uri.toString()
                    );
                    files.add(newsFile);
                }
                viewModel.sendIntent(new AddMultipleNewsFilesIntent(files));
            }
        }
    }

    private boolean isFileLessSize(Uri uri) {
        long size = ContentHelper.getFileSize(
                requireContext(),
                uri
        );
        return size / (1024 * 1024) <= 20;
    }

    private void render(NewsDetailsState state) {
        if(state instanceof NewsAddedState) {
            renderNewsAddedState((NewsAddedState)state);
        }
        else if(state instanceof ExceptionState) {
            renderExceptionState((ExceptionState)state);
        }
        else if(state instanceof NewsImagesListState) {
            renderNewsImageListState((NewsImagesListState)state);
        }
        else if(state instanceof NewsFilesState) {
            renderNewsFilesListState((NewsFilesState)state);
        }
        else if(state instanceof SendingState) {
            renderSendingState();
        }
        else {
            throw new StateIsNotSupported(state.getClass().getName());
        }
    }

    private void renderSendingState() {
        progressDialog = ProgressDialog.show(
                requireContext(),
                "Sending",
                ""
        );
    }

    private void renderNewsFilesListState(NewsFilesState state) {

        if(state.getNewsFiles().size() == 0) {
            binding.rvFiles.setVisibility(View.GONE);
        }
        else {
            binding.rvFiles.setVisibility(View.VISIBLE);
        }


        newsFilesAdapter.setItems(state.getNewsFiles());

        viewModel.refreshNewsImages();
    }

    private void renderNewsImageListState(NewsImagesListState state) {

        if(state.getNewsImages().size() == 0) {
            binding.rvImages.setVisibility(View.GONE);
        }
        else {
            binding.rvImages.setVisibility(View.VISIBLE);
        }

        newsImageAdapter.setItems(state.getNewsImages());
    }

    private void renderNewsAddedState(NewsAddedState state) {

        if(progressDialog != null) {
            progressDialog.dismiss();
        }

        FragmentResultHelper.saveResult(
                requireActivity(),
                R.id.nav_host,
                KEY_IS_SUCCESS_ADDED,
                true
        );

        Navigation.findNavController(binding.getRoot())
                .navigateUp();
    }

    private void renderExceptionState(ExceptionState state) {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        ToastUtil.showExceptionMessage(
                requireContext(),
                state.getThrowable()
        );
    }

    private void processSendNewsIntent() {

        if(isEdit) {

            News news = new News(
                    editableNews.getId(),
                    viewModel.getCurrentUserId(),
                    binding.edTitle.getText().toString(),
                    binding.edText.getText().toString(),
                    LocalDateTime.now(),
                    null,
                    null,
                    null
            );

            viewModel.sendIntent(new UpdateNewsIntent(news));
        }
        else {
            News news = new News(
                    viewModel.getCurrentUserId(),
                    binding.edTitle.getText().toString(),
                    binding.edText.getText().toString()
            );

            viewModel.sendIntent(new AddNewsIntent(news));
        }
    }

}
