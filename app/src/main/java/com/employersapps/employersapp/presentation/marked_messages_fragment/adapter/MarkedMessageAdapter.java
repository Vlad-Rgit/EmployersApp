package com.employersapps.employersapp.presentation.marked_messages_fragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.employersapps.core.domain.MarkedMessage;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.ItemListMessageBinding;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;

import org.jetbrains.annotations.NotNull;

import java.io.LineNumberInputStream;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class MarkedMessageAdapter extends RecyclerView.Adapter<MarkedMessageAdapter.ViewHolder> {


    private List<MarkedMessage> items = Collections.emptyList();

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        ItemListMessageBinding binding = ItemListMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );


        ViewHolder holder = new ViewHolder(binding);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MarkedMessageAdapter.ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<MarkedMessage> newItems) {

        SimpleDiffCallback<MarkedMessage> callback = new SimpleDiffCallback<MarkedMessage>(
                items,
                newItems
        );

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        items = newItems;

        result.dispatchUpdatesTo(this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemListMessageBinding binding;

        public ViewHolder(ItemListMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MarkedMessage message) {


            binding.tvSenderName.setText(message.getSender());
            binding.tvBody.setText(message.getText());
            binding.tvDateSended.setText(
                    message.getTimestamp()
                            .format(DateTimeFormatter.ISO_DATE)
            );


            if(message.isPrivate()) {

                Glide.with(binding.img)
                        .load(Config.API_ADDRESS + "/static/employers/"
                         + message.getLogoPath())
                        .into(binding.img);
            }
            else {
                Glide.with(binding.img)
                        .load(Config.API_ADDRESS + "/static/logos/"
                                + message.getLogoPath())
                        .into(binding.img);
            }

        }
    }



}
