package com.employersapps.employersapp.presentation.messages_fragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.employersapps.core.domain.MessageAttachment;
import com.employersapps.employersapp.databinding.ItemFileBinding;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;

import java.util.Collections;
import java.util.List;


public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.AttachmentViewHolder> {

    public interface OnRemoveClickedListener {
        void onRemoveClicked(MessageAttachment messageAttachment);
    }

    private List<MessageAttachment> items = Collections.emptyList();
    private OnRemoveClickedListener onRemoveClickedListener;

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AttachmentViewHolder holder = new AttachmentViewHolder(
                ItemFileBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );

        holder.setOnRemoveClickedListener(new OnRemoveClickedListener() {
            @Override
            public void onRemoveClicked(MessageAttachment messageAttachment) {
                if(onRemoveClickedListener != null) {
                    onRemoveClickedListener.onRemoveClicked(messageAttachment);
                }
            }
        });

        return holder;
    }

    public void setOnRemoveClickedListener(OnRemoveClickedListener onRemoveClickedListener) {
        this.onRemoveClickedListener = onRemoveClickedListener;
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<MessageAttachment> newItems) {
        SimpleDiffCallback diffCallback = new SimpleDiffCallback(
                items, newItems
        );
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    public static class AttachmentViewHolder extends RecyclerView.ViewHolder {

        private final ItemFileBinding binding;
        private OnRemoveClickedListener onRemoveClickedListener;
        private MessageAttachment currentItem;

        public AttachmentViewHolder(ItemFileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.btnRemoveFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentItem != null && onRemoveClickedListener != null) {
                        onRemoveClickedListener.onRemoveClicked(currentItem);
                    }
                }
            });
        }

        public void setOnRemoveClickedListener(OnRemoveClickedListener onRemoveClickedListener) {
            this.onRemoveClickedListener = onRemoveClickedListener;
        }

        public void bind(MessageAttachment attachment) {
            currentItem = attachment;
            binding.tvFileName.setText(attachment.getName());
        }
    }
}
