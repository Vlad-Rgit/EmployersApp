package com.employersapps.employersapp.presentation.messages_fragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.LastMessageChat;
import com.employersapps.core.domain.Message;
import com.employersapps.core.domain.MessageAttachment;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.ItemListMessageBinding;
import com.employersapps.employersapp.databinding.ItemNewsFileListBinding;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.function.ObjIntConsumer;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {


    public interface OnMessageClickedListener {
        void onMessageClicked(LastMessageChat message);
    }

    private List<LastMessageChat> items = Collections.emptyList();
    private long currentUserId;
    private OnMessageClickedListener onMessageClickedListener;

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListMessageBinding binding = ItemListMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        MessageViewHolder holder = new MessageViewHolder(binding, currentUserId);

        holder.setOnMessageClickedListener(new OnMessageClickedListener() {
            @Override
            public void onMessageClicked(LastMessageChat message) {
                if(onMessageClickedListener != null) {
                    onMessageClickedListener.onMessageClicked(message);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<LastMessageChat> newItems) {
        SimpleDiffCallback<LastMessageChat> diffCallback = new SimpleDiffCallback<LastMessageChat>(
                items, newItems
        );
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    public void setCurrentUserId(long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setOnMessageClickedListener(OnMessageClickedListener onMessageClickedListener) {
        this.onMessageClickedListener = onMessageClickedListener;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder  {

        private final ItemListMessageBinding binding;
        private LastMessageChat currentItem;
        private OnMessageClickedListener onMessageClickedListener;

        private long currentUserId;

        public MessageViewHolder(ItemListMessageBinding binding, long currentUserId) {

            super(binding.getRoot());
            this.binding = binding;
            this.currentUserId = currentUserId;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onMessageClickedListener != null &&
                        currentItem != null) {
                        onMessageClickedListener.onMessageClicked(currentItem);
                    }
                }
            });
        }

        public void bind(LastMessageChat message) {

            currentItem = message;

            if(message.isInPrivateChat()) {
                if(currentUserId == message.getSenderId()) {
                    binding.tvSenderName.setText(
                            message.getReceiverFirstName() + " " + message.getReceiverLastName());
                }
                else {
                    binding.tvSenderName.setText(
                            message.getSenderFirstName() + " " + message.getSenderLastName()
                    );
                }
            }
            else {
                binding.tvSenderName.setText(message.getName());
            }
            binding.tvBody.setText(message.getText());

            LocalDateTime timestamp = message.getTimestamp();
            LocalDate date = timestamp.toLocalDate();
            LocalDate now = LocalDate.now();

            if(date.equals(now)) {
                binding.tvDateSended.setText(timestamp.format(
                        DateTimeFormatter.ofLocalizedTime(
                                FormatStyle.SHORT
                        )
                ));
            }
            else {
                binding.tvDateSended.setText(timestamp.format(
                        DateTimeFormatter.ofLocalizedDate(
                                FormatStyle.SHORT)
                ));
            }

            if(message.getSenderPhotoPath() == null && message.isInPrivateChat()) {
                Glide.with(binding.img)
                        .load(R.drawable.no_image)
                        .into(binding.img);
            }
            else if(message.getLogoPath() == null && !message.isInPrivateChat()) {
                Glide.with(binding.img)
                        .load(R.drawable.chat_logo)
                        .into(binding.img);
            }
            else {

                String endpoint;

                if(message.isInPrivateChat()) {
                    if(message.getSenderId() == currentUserId) {
                        endpoint = "/static/employers/" +
                                message.getReceiverPhotoPath();
                    }
                    else {
                        endpoint = "/static/employers/" +
                                message.getSenderPhotoPath();
                    }
                }
                else {
                    endpoint = "/static/logos/" +
                        message.getLogoPath();
                }

                String imgUrl = Config.API_ADDRESS + endpoint;

                Glide.with(binding.img)
                        .load(imgUrl)
                        .error(R.drawable.no_image)
                        .into(binding.img);
            }
        }

        public void setOnMessageClickedListener(OnMessageClickedListener onMessageClickedListener) {
            this.onMessageClickedListener = onMessageClickedListener;
        }
    }
}
