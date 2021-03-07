package com.employersapps.employersapp.presentation.news_fragment.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.employersapps.core.data.UserDataSource;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.News;
import com.employersapps.core.domain.NewsFile;
import com.employersapps.core.domain.NewsImage;
import com.employersapps.core.domain.User;
import com.employersapps.core.domain.network.NewsFileNetwork;
import com.employersapps.core.domain.network.NewsImageNetwork;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.ItemNewsFileListBinding;
import com.employersapps.employersapp.databinding.NewsItemBinding;
import com.employersapps.employersapp.framework.util.ContentHelper;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {


    public interface OnFileClickedListener {
        void onFileClicked(NewsFileNetwork newsFileNetwork);
    }

    public interface OnMenuItemClickedListener {
        void onMenuItemClicked(News news);
    }


    private List<News> items = new ArrayList<>();
    private OnFileClickedListener onFileClickedListener;
    private OnMenuItemClickedListener onDeleteClicked;
    private OnMenuItemClickedListener onEditClicked;
    private UserDataSource userDataSource;


    public NewsAdapter(Context context) {
        userDataSource = ((EmployersApp)context.getApplicationContext())
                .getAppComponent()
                .getUserDataSource();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NewsItemBinding binding = NewsItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        NewsViewHolder viewHolder = new NewsViewHolder(binding);

        viewHolder.setOnFileClickedListener(new OnFileClickedListener() {
            @Override
            public void onFileClicked(NewsFileNetwork newsFileNetwork) {
                if(onFileClickedListener != null) {
                    onFileClickedListener.onFileClicked(newsFileNetwork);
                }
            }
        });

        viewHolder.setOnEditClicked(new OnMenuItemClickedListener() {
            @Override
            public void onMenuItemClicked(News news) {
                if(onEditClicked != null) {
                    onEditClicked.onMenuItemClicked(news);
                }
            }
        });

        viewHolder.setOnDeleteClicked(new OnMenuItemClickedListener() {
            @Override
            public void onMenuItemClicked(News news) {
                if(onDeleteClicked != null) {
                    onDeleteClicked.onMenuItemClicked(news);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bind(items.get(position), userDataSource.getCurrentUser());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnFileClickedListener(OnFileClickedListener onFileClickedListener) {
        this.onFileClickedListener = onFileClickedListener;
    }

    public void setOnDeleteClicked(OnMenuItemClickedListener onDeleteClicked) {
        this.onDeleteClicked = onDeleteClicked;
    }

    public void setOnEditClicked(OnMenuItemClickedListener onEditClicked) {
        this.onEditClicked = onEditClicked;
    }

    public void setItems(List<News> newItems) {

        newItems = newItems.stream().sorted(
                new Comparator<News>() {
                    @Override
                    public int compare(News o1, News o2) {
                        return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                    }
                }
        ).collect(Collectors.toList());

        SimpleDiffCallback<News> diffCallback =
                new SimpleDiffCallback<>(items, newItems);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        private final NewsItemBinding binding;
        private final Context context;
        private News currentNews;
        private OnFileClickedListener onFileClickedListener;
        private OnMenuItemClickedListener onDeleteClicked;
        private OnMenuItemClickedListener onEditClicked;


        public NewsViewHolder(@NonNull NewsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();
            binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    binding.lvToggleCircles.setCircleOnPosition(position);
                }
            });

            binding.btnShowImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageViewer(context);
                }
            });

            binding.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(
                            v.getContext(),
                            binding.btnMenu
                    );

                    popupMenu.inflate(R.menu.news_item_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_edit: {
                                    if(onEditClicked != null) {
                                        onEditClicked.onMenuItemClicked(currentNews);
                                    }
                                    return true;
                                }
                                case R.id.menu_delete: {
                                    if(onDeleteClicked != null) {
                                        onDeleteClicked.onMenuItemClicked(currentNews);
                                    }
                                    return true;
                                }
                                default: return false;
                            }
                        }
                    });

                    popupMenu.show();
                }
            });

            initFilesRecyclerView();
        }

        private void initFilesRecyclerView() {
            RecyclerView rvFiles = binding.rvFiles;

            rvFiles.setHasFixedSize(true);
            rvFiles.setLayoutManager(new LinearLayoutManager(
                    binding.getRoot().getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            ));
        }

        public void setOnFileClickedListener(OnFileClickedListener onFileClickedListener) {
            this.onFileClickedListener = onFileClickedListener;
        }

        private void showImageViewer(Context context) {
            if (currentNews != null && currentNews.getNewsPhotos().size() > 0) {
                StfalconImageViewer.Builder<NewsImageNetwork> builder =
                        new StfalconImageViewer.Builder<NewsImageNetwork>(context,
                                currentNews.getNewsPhotos(),
                                new ImageLoader<NewsImageNetwork>() {
                                    @Override
                                    public void loadImage(ImageView imageView, NewsImageNetwork image) {
                                        Glide.with(context)
                                                .load(Config.API_ADDRESS + "/static/news/" + image.getName())
                                                .into(imageView);
                                    }
                                });
                builder.withStartPosition(binding.viewPager.getCurrentItem());
                builder.show();
            }
        }


        public void setOnDeleteClicked(OnMenuItemClickedListener onDeleteClicked) {
            this.onDeleteClicked = onDeleteClicked;
        }

        public void setOnEditClicked(OnMenuItemClickedListener onEditClicked) {
            this.onEditClicked = onEditClicked;
        }

        public void bind(News news, User currentUser) {

            currentNews = news;

            binding.tvText.setText(news.getText());
            binding.tvTitle.setText(news.getTitle());

            Employer author = news.getUser();

            if(author != null) {
                binding.tvAuthor.setText(author.getFullName());
            }

            DateTimeFormatter format = DateTimeFormatter.ISO_DATE;

            binding.tvDate.setText(
                    news.getCreatedAt().format(format)
            );

            int newsPhotoSize = news.getNewsPhotos().size();
            if(newsPhotoSize > 0) {
                binding.viewPager.setVisibility(View.VISIBLE);
                binding.viewPager.setAdapter(
                        new NewsImageAdapter(news.getNewsPhotos())
                );

                binding.btnShowImages.setVisibility(View.VISIBLE);

                if(newsPhotoSize > 1) {
                    binding.lvToggleCircles.setVisibility(View.VISIBLE);
                    binding.lvToggleCircles.setCirclesCount(newsPhotoSize);
                }
                else {
                    binding.lvToggleCircles.setVisibility(View.GONE);
                }
            }
            else {
                binding.viewPager.setVisibility(View.GONE);
                binding.viewPager.setAdapter(null);
                binding.lvToggleCircles.setVisibility(View.GONE);
                binding.btnShowImages.setVisibility(View.GONE);
            }

            if(news.getNewsFiles().size() > 0) {
                binding.rvFiles.setVisibility(View.VISIBLE);
                NewsFileAdapter adapter = new NewsFileAdapter(news.getNewsFiles());
                adapter.setOnFileClickedListener(new OnFileClickedListener() {
                    @Override
                    public void onFileClicked(NewsFileNetwork newsFileNetwork) {
                        if(onFileClickedListener != null) {
                            onFileClickedListener.onFileClicked(newsFileNetwork);
                        }
                    }
                });
                binding.rvFiles.setAdapter(adapter);
            }
            else {
                binding.rvFiles.setVisibility(View.GONE);
                binding.rvFiles.setAdapter(null);
            }

            if(currentUser.getRoleId() != 1) {
                binding.btnMenu.setVisibility(View.GONE);
            }
        }

        public static class NewsFileAdapter extends RecyclerView.Adapter<NewsFileAdapter.NewsFileViewHolder> {

            private final List<NewsFileNetwork> newsFiles;
            private OnFileClickedListener onFileClickedListener;

            public NewsFileAdapter(List<NewsFileNetwork> newsFiles) {
                this.newsFiles = newsFiles;
            }

            @NonNull
            @Override
            public NewsFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ItemNewsFileListBinding binding = ItemNewsFileListBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );
                NewsFileViewHolder viewHolder = new NewsFileViewHolder(binding);

                viewHolder.setOnFileClickedListener(new OnFileClickedListener() {
                    @Override
                    public void onFileClicked(NewsFileNetwork newsFileNetwork) {
                        if(onFileClickedListener != null) {
                            onFileClickedListener.onFileClicked(newsFileNetwork);
                        }
                    }
                });

                return viewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull NewsFileViewHolder holder, int position) {
                holder.bind(newsFiles.get(position));
            }

            @Override
            public int getItemCount() {
                return newsFiles.size();
            }

            public void setOnFileClickedListener(OnFileClickedListener onFileClickedListener) {
                this.onFileClickedListener = onFileClickedListener;
            }

            public static class NewsFileViewHolder extends RecyclerView.ViewHolder {

                private final ItemNewsFileListBinding binding;
                private NewsFileNetwork currentItem;
                private OnFileClickedListener onFileClickedListener;

                public NewsFileViewHolder(@NonNull ItemNewsFileListBinding binding) {
                    super(binding.getRoot());
                    this.binding  = binding;
                    this.binding.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(onFileClickedListener != null) {
                                onFileClickedListener.onFileClicked(currentItem);
                            }
                        }
                    });
                }

                public void setOnFileClickedListener(OnFileClickedListener onFileClickedListener) {
                    this.onFileClickedListener = onFileClickedListener;
                }

                public void bind(NewsFileNetwork newsFileNetwork) {
                    currentItem = newsFileNetwork;
                    binding.tvFileName.setText(
                            newsFileNetwork.getOriginalFileName());
                }
            }
        }


        public static class NewsImageAdapter extends RecyclerView.Adapter<NewsImageAdapter.NewsImageHolder> {

            public interface OnImageClickedListener {
                void onImageClicked(int position);
            }

            private final List<NewsImageNetwork> newsImages;

            public NewsImageAdapter(List<NewsImageNetwork> newsImages) {
                this.newsImages = newsImages;
            }

            @NonNull
            @Override
            public NewsImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.news_image,
                                parent,
                                false
                        );


                NewsImageHolder holder = new NewsImageHolder(view);

                holder.setOnImageClickedListener(new OnImageClickedListener() {
                    @Override
                    public void onImageClicked(int position) {
                        showImageViewer(view.getContext(), position);
                    }
                });

                return holder;
            }


            private void showImageViewer(Context context, int position) {
                if (newsImages.size() > 0) {
                    StfalconImageViewer.Builder<NewsImageNetwork> builder =
                            new StfalconImageViewer.Builder<NewsImageNetwork>(context,
                                    newsImages,
                                    new ImageLoader<NewsImageNetwork>() {
                                        @Override
                                        public void loadImage(ImageView imageView, NewsImageNetwork image) {
                                            Glide.with(context)
                                                    .load(Config.API_ADDRESS + "/static/news/" + image.getName())
                                                    .into(imageView);
                                        }
                                    });
                    builder.withStartPosition(position);
                    builder.show();
                }
            }

            @Override
            public void onBindViewHolder(@NonNull NewsImageHolder holder, int position) {
                holder.bind(newsImages.get(position), position);
            }

            @Override
            public int getItemCount() {
                return newsImages.size();
            }

            public static class NewsImageHolder extends RecyclerView.ViewHolder {

                private final ImageView imageView;
                private int position = 0;
                private OnImageClickedListener onImageClickedListener;

                public NewsImageHolder(View view) {
                    super(view);
                    this.imageView = view.findViewById(R.id.img);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(onImageClickedListener != null) {
                                onImageClickedListener.onImageClicked(position);
                            }
                        }
                    });
                }

                public void bind(NewsImageNetwork image, int position) {
                    this.position = position;
                    String url = Config.API_ADDRESS + "/static/news/" + image.getName();
                    Glide.with(imageView)
                            .load(url)
                            .into(imageView);
                }

                public void setOnImageClickedListener(OnImageClickedListener onImageClickedListener) {
                    this.onImageClickedListener = onImageClickedListener;
                }
            }
        }
    }
}
