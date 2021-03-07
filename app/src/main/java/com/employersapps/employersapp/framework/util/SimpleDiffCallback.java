package com.employersapps.employersapp.framework.util;

import androidx.recyclerview.widget.DiffUtil;

import com.employersapps.core.domain.ListItem;

import java.util.List;

public class SimpleDiffCallback<T extends ListItem<T>>
        extends DiffUtil.Callback {

    private final List<T> oldList;
    private final List<T> newList;

    public SimpleDiffCallback(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition)
                .areItemsTheSame(newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition)
                .areContentsTheSame(newList.get(newItemPosition));
    }
}
