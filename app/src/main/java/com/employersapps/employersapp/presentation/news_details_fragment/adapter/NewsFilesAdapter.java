package com.employersapps.employersapp.presentation.news_details_fragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.employersapps.core.domain.NewsFile;
import com.employersapps.employersapp.databinding.ItemFileBinding;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;

import java.util.Collections;
import java.util.List;

public class NewsFilesAdapter extends RecyclerView.Adapter<NewsFilesAdapter.NewsFileViewHolder> {

    public interface OnRemoveClickedListener {
        void onRemoveClicked(NewsFile newsFile);
    }

    private List<NewsFile> items = Collections.emptyList();
    private OnRemoveClickedListener onRemoveClickedListener;

    @NonNull
    @Override
    public NewsFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemFileBinding binding = ItemFileBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        NewsFileViewHolder holder =  new NewsFileViewHolder(binding);

        holder.setOnRemoveClickedListener(new OnRemoveClickedListener() {
            @Override
            public void onRemoveClicked(NewsFile newsFile) {
                if(onRemoveClickedListener != null) {
                    onRemoveClickedListener.onRemoveClicked(newsFile);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsFileViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnRemoveClickedListener(OnRemoveClickedListener onRemoveClickedListener) {
        this.onRemoveClickedListener = onRemoveClickedListener;
    }

    public void setItems(List<NewsFile> newItems) {
        SimpleDiffCallback<NewsFile> diffCallback = new SimpleDiffCallback<>(
                items, newItems
        );
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    public static class NewsFileViewHolder extends RecyclerView.ViewHolder {

        private final ItemFileBinding binding;
        private OnRemoveClickedListener onRemoveClickedListener;

        private NewsFile currentItem;

        public NewsFileViewHolder(ItemFileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.btnRemoveFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onRemoveClickedListener != null) {
                        onRemoveClickedListener.onRemoveClicked(currentItem);
                    }
                }
            });
        }

        public void bind(NewsFile newsFile) {
            currentItem = newsFile;
            binding.tvFileName.setText(newsFile.getName());
        }

        public void setOnRemoveClickedListener(OnRemoveClickedListener onRemoveClickedListener) {
            this.onRemoveClickedListener = onRemoveClickedListener;
        }
    }
}
