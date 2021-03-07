package com.employersapps.employersapp.presentation.news_fragment.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.employersapps.core.domain.News;
import com.employersapps.core.domain.network.NewsFileNetwork;
import com.employersapps.core.utils.StateIsNotSupported;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.FragmentNewsBinding;
import com.employersapps.employersapp.framework.util.FragmentResultHelper;
import com.employersapps.employersapp.framework.util.PaddingDecorator;
import com.employersapps.employersapp.framework.util.ToastUtil;
import com.employersapps.employersapp.presentation.news_details_fragment.ui.NewsDetailsFragment;
import com.employersapps.employersapp.presentation.news_fragment.adapter.NewsAdapter;
import com.employersapps.employersapp.presentation.news_fragment.intents.DeleteNewsIntent;
import com.employersapps.employersapp.presentation.news_fragment.intents.RefreshNewsIntent;
import com.employersapps.employersapp.presentation.news_fragment.states.ExceptionState;
import com.employersapps.employersapp.presentation.news_fragment.states.InitState;
import com.employersapps.employersapp.presentation.news_fragment.states.ListNewsState;
import com.employersapps.employersapp.presentation.news_fragment.states.NewsFragmentState;
import com.employersapps.employersapp.presentation.news_fragment.viewmodel.NewsFragmentViewModel;

import java.net.SocketTimeoutException;

public class NewsFragment extends Fragment {

    private static final int REQUEST_WRITE_PERMISSION = 1;

    private FragmentNewsBinding binding;
    private NewsFragmentViewModel viewModel;
    private NewsAdapter newsAdapter;
    private NewsFileNetwork pendingFileDownload;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(
                        requireActivity().getApplication()
                )
        ).get(NewsFragmentViewModel.class);

        viewModel.sendIntent(new RefreshNewsIntent());

        newsAdapter = new NewsAdapter(requireContext());

        newsAdapter.setOnFileClickedListener(this::startDownloadFile);
        newsAdapter.setOnDeleteClicked(this::deleteNews);
        newsAdapter.setOnEditClicked(this::editNews);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentNewsBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSwipeRefreshLayout();
        initRecyclerView();
        initButtons();
        observeViewModel();
        observerFragmentResults();
    }

    private void startDownloadFile(NewsFileNetwork newsFileNetwork) {
        if(ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            pendingFileDownload = newsFileNetwork;
            requestWritePermission();
        }
        else {
            downloadFile(newsFileNetwork);
        }
    }

    private void editNews(News news) {
        Bundle args = new Bundle();
        args.putSerializable(NewsDetailsFragment.EXTRA_EDITABLE_NEWS, news);
        Navigation.findNavController(
                requireActivity(),
                R.id.nav_host
        ).navigate(
                R.id.action_mainFragment_to_newsDetailsFragment,
                args
        );
    }

    private void deleteNews(News news) {
        viewModel.sendIntent(new DeleteNewsIntent(news));
    }

    private void downloadFile(NewsFileNetwork newsFileNetwork) {

        Uri uri = Uri.parse(Config.API_ADDRESS +
                "/static/files/news/" +
                newsFileNetwork.getName());

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(newsFileNetwork.getOriginalFileName());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, newsFileNetwork.getOriginalFileName());

        DownloadManager downloadManager = (DownloadManager)
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE);

        downloadManager.enqueue(request);

        ToastUtil.showLongToast(
                requireContext(),
                R.string.download_started
        );

    }

    private void requestWritePermission() {
        requestPermissions(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_PERMISSION
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_WRITE_PERMISSION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile(pendingFileDownload);
            pendingFileDownload = null;
        }
    }

    private void observerFragmentResults() {
        FragmentResultHelper.getResultLiveData(
                requireActivity(),
                R.id.nav_host,
                NewsDetailsFragment.KEY_IS_SUCCESS_ADDED
        ).observe(getViewLifecycleOwner(), new Observer<Object>() {
            @Override
            public void onChanged(Object value) {
                if(value instanceof Boolean) {
                    Boolean isSuccess = (Boolean)value;
                    if(isSuccess) {
                        viewModel.sendIntent(new RefreshNewsIntent());
                    }
                }
            }
        });
    }

    private void initButtons() {
        if(viewModel.isAdmin()) {
            binding.btnAddNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(
                           requireActivity(),
                            R.id.nav_host
                    ).navigate(
                                R.id.action_mainFragment_to_newsDetailsFragment
                            );
                }
            });
        }
        else {
            binding.btnAddNews.setVisibility(View.GONE);
        }
    }

    private void initSwipeRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.sendIntent(new RefreshNewsIntent());
            }
        });
    }

    private void observeViewModel() {
        viewModel.getStateLiveData().observe(getViewLifecycleOwner(), new Observer<NewsFragmentState>() {
            @Override
            public void onChanged(NewsFragmentState newsFragmentState) {
                render(newsFragmentState);
            }
        });
    }


    private void render(NewsFragmentState state) {
        if(state instanceof InitState) {
            renderInitState();
        }
        else if(state instanceof ListNewsState) {
            renderListNewsState((ListNewsState)state);
        }
        else if(state instanceof ExceptionState) {
            renderExceptionState((ExceptionState)state);
        }
        else {
            throw new StateIsNotSupported(state.getClass().getName());
        }
    }

    private void renderExceptionState(ExceptionState state) {

        binding.refreshLayout.setRefreshing(false);

        Throwable t = state.getThrowable();

        if(t instanceof SocketTimeoutException) {
            ToastUtil.showLongToast(
                    requireContext(),
                    R.string.server_timeout
            );
        }
        else {
            ToastUtil.showLongToast(
                    requireContext(),
                    t.getLocalizedMessage()
            );
        }
    }

    private void initRecyclerView() {

        RecyclerView rvNews = binding.rvNews;
        Context context = requireContext();

        rvNews.setHasFixedSize(true);
        rvNews.setLayoutManager(new LinearLayoutManager(context));
        rvNews.setAdapter(newsAdapter);

        int padding = (int)context.getResources().getDimension(
                R.dimen.default_padding
        ) / 2;

        rvNews.addItemDecoration(new PaddingDecorator(
                padding,
                padding,
                0,
                0
        ));
    }

    private void renderInitState() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        binding.rvNews.setVisibility(View.GONE);
    }

    private void renderListNewsState(ListNewsState state) {

        binding.progressCircular.setVisibility(View.GONE);
        binding.rvNews.setVisibility(View.VISIBLE);
        binding.refreshLayout.setRefreshing(false);

        newsAdapter.setItems(state.getNews());
    }
}
