package com.employersapps.employersapp.presentation.employers_choice_list.adapters;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.MutableSelection;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.employersapps.core.domain.Employer;
import com.employersapps.employersapp.databinding.ItemChoiceEmployerBinding;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployerMultipleChoiceAdapter extends RecyclerView.Adapter<EmployerMultipleChoiceAdapter.ViewHolder> {

    private static final String KEY_SELECTION_TRACKER = "multipleEmployerChoice";

    private List<Employer> items = Collections.emptyList();
    private List<Employer> selectedItems = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public interface OnIsSelectedChangedListener {
            void onIsSelectedChanged(Employer employer, boolean isSelected);
        }

        private Employer currentItem;
        private OnIsSelectedChangedListener onIsSelectedChangedListener;
        private final ItemChoiceEmployerBinding binding;

        private final ItemDetailsLookup.ItemDetails<Long> itemDetails =
                new ItemDetailsLookup.ItemDetails<Long>() {
            @Override
            public int getPosition() {
                return getAdapterPosition();
            }

            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            public Long getSelectionKey() {
                return currentItem.getId();
            }
        };

        public ViewHolder(ItemChoiceEmployerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(onIsSelectedChangedListener != null) {
                        onIsSelectedChangedListener.onIsSelectedChanged(currentItem, isChecked);
                    }
                }
            });
        }

        public void setOnIsSelectedChangedListener(OnIsSelectedChangedListener onIsSelectedChangedListener) {
            this.onIsSelectedChangedListener = onIsSelectedChangedListener;
        }

        public void bind(Employer employer, boolean isSelected) {
            currentItem = employer;

            binding.tvEmployerName.setText(
                    currentItem.getFullName()
            );

            binding.checkbox.setChecked(isSelected);
        }
    }


    public void setItems(List<Employer> newItems) {
        SimpleDiffCallback<Employer> diffCallback = new SimpleDiffCallback<>(
                items,
                newItems
        );
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback);
        items = newItems;
        result.dispatchUpdatesTo(this);
    }

    public List<Employer> getSelectedEmployers() {
        return selectedItems;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        ItemChoiceEmployerBinding binding = ItemChoiceEmployerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        ViewHolder holder = new ViewHolder(binding);

        holder.setOnIsSelectedChangedListener(new ViewHolder.OnIsSelectedChangedListener() {
            @Override
            public void onIsSelectedChanged(Employer employer, boolean isSelected) {
                if(isSelected) {
                    selectedItems.add(employer);
                }
                else {
                    selectedItems.remove(employer);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EmployerMultipleChoiceAdapter.ViewHolder holder,
                                 int position) {
        Employer employer = items.get(position);
        holder.bind(employer, selectedItems.contains(employer));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
