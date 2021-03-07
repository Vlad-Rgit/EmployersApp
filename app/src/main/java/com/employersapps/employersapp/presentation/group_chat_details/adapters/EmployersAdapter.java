package com.employersapps.employersapp.presentation.group_chat_details.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.employersapps.core.domain.Employer;
import com.employersapps.employersapp.databinding.ItemEmployerGroupChatBinding;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class EmployersAdapter extends RecyclerView.Adapter<EmployersAdapter.EmployerViewHolder> {

    public interface OnEmployerClickedListener {
        void onEmployerClicked(Employer employer);
    }



    public static class EmployerViewHolder extends RecyclerView.ViewHolder {

        private final ItemEmployerGroupChatBinding binding;

        private OnEmployerClickedListener onDeleteClickedListener;
        private Employer currentItem;
        private final boolean isReadOnly;

        public EmployerViewHolder(ItemEmployerGroupChatBinding binding, boolean isReadOnly) {

            super(binding.getRoot());
            this.binding = binding;
            this.isReadOnly = isReadOnly;

            if(isReadOnly) {
                binding.btnRemove.setVisibility(View.GONE);
            }
            else {
                binding.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onDeleteClickedListener != null) {
                            onDeleteClickedListener.onEmployerClicked(currentItem);
                        }
                    }
                });
            }
        }

        public void bind(Employer employer, long chatCreatorId) {

            currentItem = employer;
            binding.tvEmployerName.setText(
                    employer.getFullName()
            );


            if(!isReadOnly) {
                if (employer.getId() == chatCreatorId) {
                    binding.btnRemove.setVisibility(View.GONE);
                } else {
                    binding.btnRemove.setVisibility(View.VISIBLE);
                }
            }

        }

        public void setOnDeleteClickedListener(OnEmployerClickedListener onDeleteClickedListener) {
            this.onDeleteClickedListener = onDeleteClickedListener;
        }
    }

    private List<Employer> items = Collections.emptyList();
    private OnEmployerClickedListener onDeleteClickedListener;
    private long chatCreatorId;

    private final boolean isReadOnly;


    public EmployersAdapter(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public void setChatCreatorId(long chatCreatorId) {
        this.chatCreatorId = chatCreatorId;
    }


    @NonNull
    @NotNull
    @Override
    public EmployerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemEmployerGroupChatBinding binding = ItemEmployerGroupChatBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        EmployerViewHolder holder = new EmployerViewHolder(binding, isReadOnly);


        holder.setOnDeleteClickedListener(new OnEmployerClickedListener() {
            @Override
            public void onEmployerClicked(Employer employer) {
                if(onDeleteClickedListener != null) {
                    onDeleteClickedListener.onEmployerClicked(employer);
                }
            }
        });

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull EmployersAdapter.EmployerViewHolder holder, int position) {
        holder.bind(items.get(position), chatCreatorId);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnDeleteClickedListener(OnEmployerClickedListener onDeleteClickedListener) {
        this.onDeleteClickedListener = onDeleteClickedListener;
    }

    public void setItems(List<Employer> newList) {
        SimpleDiffCallback<Employer> diffCallback = new SimpleDiffCallback<>(
                items,
                newList
        );
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback);
        items = newList;
        result.dispatchUpdatesTo(this);
    }

}
