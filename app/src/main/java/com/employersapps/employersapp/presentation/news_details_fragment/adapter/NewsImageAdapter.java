package com.employersapps.employersapp.presentation.news_details_fragment.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.employersapps.core.domain.NewsImage;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.NewsImageItemBinding;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class NewsImageAdapter extends RecyclerView.Adapter<NewsImageAdapter.NewsImageViewHolder> {


    public interface OnRemoveImageCallback {
        void onRemoveImage(NewsImage newsImage);
    }

    private List<NewsImage> items = Collections.emptyList();
    private OnRemoveImageCallback onRemoveImageCallback;

    @NonNull
    @Override
    public NewsImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        NewsImageItemBinding binding = NewsImageItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        NewsImageViewHolder holder = new NewsImageViewHolder(binding);

        holder.setOnRemoveImageCallback(new OnRemoveImageCallback() {
            @Override
            public void onRemoveImage(NewsImage newsImage) {
                if(onRemoveImageCallback != null) {
                    onRemoveImageCallback.onRemoveImage(newsImage);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsImageViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<NewsImage> newItems) {
        Log.i("MyInfo", "Set items");
        SimpleDiffCallback<NewsImage> diffCallback = new SimpleDiffCallback<>(items, newItems);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    public void setOnRemoveImageCallback(OnRemoveImageCallback onRemoveImageCallback) {
        this.onRemoveImageCallback = onRemoveImageCallback;
    }

    public static class NewsImageViewHolder extends RecyclerView.ViewHolder {

        private final NewsImageItemBinding binding;
        private final Context context;
        private OnRemoveImageCallback onRemoveImageCallback;

        private NewsImage currentNewsImage;

        public NewsImageViewHolder(@NonNull NewsImageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();

            binding.btnRemoveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onRemoveImageCallback != null && currentNewsImage != null) {
                        onRemoveImageCallback.onRemoveImage(currentNewsImage);
                    }
                }
            });
        }

        public void bind(NewsImage newsImage) {

            currentNewsImage = newsImage;

            int cornerRadius = (int)context.getResources()
                    .getDimension(R.dimen.corner_radius);

            if(newsImage.isNetwork()) {
                Glide.with(context)
                        .load(Config.API_ADDRESS + "/static/news/" + newsImage.getUri())
                        .transform(new RoundedCornersTransformation(cornerRadius,
                                0,
                                RoundedCornersTransformation.CornerType.ALL))
                        .placeholder(buildCircularProgress())
                        .into(binding.img);
            }
            else {
                Glide.with(context)
                        .load(Uri.parse(newsImage.getUri()))
                        .transform(new RoundedCornersTransformation(cornerRadius,
                                0,
                                RoundedCornersTransformation.CornerType.ALL))
                        .placeholder(buildCircularProgress())
                        .into(binding.img);
            }


        }

        private CircularProgressDrawable buildCircularProgress() {
            CircularProgressDrawable progress = new CircularProgressDrawable(context);
            progress.setStrokeWidth(1);
            progress.start();
            return progress;
        }

        public void setOnRemoveImageCallback(OnRemoveImageCallback onRemoveImageCallback) {
            this.onRemoveImageCallback = onRemoveImageCallback;
        }
    }




}
