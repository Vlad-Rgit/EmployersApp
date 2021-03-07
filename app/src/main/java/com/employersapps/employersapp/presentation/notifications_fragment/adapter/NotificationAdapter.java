package com.employersapps.employersapp.presentation.notifications_fragment.adapter;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.employersapps.core.domain.UserNotification;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.ItemNotificationBinding;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;
import com.employersapps.employersapp.presentation.notifications_fragment.viewmodel.NotificationsViewModel;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {


    public interface OnNotificationClick {
        void onNotificationClick(UserNotification notification);
    }

    private OnNotificationClick onEditClick;
    private OnNotificationClick onRemoveClick;

    private List<UserNotification> items = Collections.emptyList();

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        NotificationViewHolder holder = new NotificationViewHolder(binding);

        holder.setOnEditClick(new OnNotificationClick() {
            @Override
            public void onNotificationClick(UserNotification notification) {
                if(onEditClick != null) {
                    onEditClick.onNotificationClick(notification);
                }
            }
        });

        holder.setOnRemoveClick(new OnNotificationClick() {
            @Override
            public void onNotificationClick(UserNotification notification) {
                if(onRemoveClick != null) {
                    onRemoveClick.onNotificationClick(notification);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<UserNotification> newItems) {

        SimpleDiffCallback<UserNotification> diffCallback = new SimpleDiffCallback<>(
                items,
                newItems
        );

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        items = newItems;

        diffResult.dispatchUpdatesTo(this);
    }

    public void setOnEditClick(OnNotificationClick onEditClick) {
        this.onEditClick = onEditClick;
    }

    public void setOnRemoveClick(OnNotificationClick onRemoveClick) {
        this.onRemoveClick = onRemoveClick;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final ItemNotificationBinding binding;

        private UserNotification currentNotification;

        private OnNotificationClick onEditClick;
        private OnNotificationClick onRemoveClick;

        private PopupMenu popupMenu;

        public NotificationViewHolder(@NonNull ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPopupMenu().show();
                }
            });
        }


        private synchronized PopupMenu getPopupMenu() {
            if(popupMenu == null) {
                popupMenu = new PopupMenu(
                        binding.getRoot().getContext(),
                        binding.btnMenu
                );
                popupMenu.inflate(R.menu.notification_item_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit: {
                                if(onEditClick != null) {
                                    onEditClick.onNotificationClick(currentNotification);
                                }
                                return true;
                            }
                            case R.id.menu_remove: {
                                if(onRemoveClick != null) {
                                    onRemoveClick.onNotificationClick(currentNotification);
                                }
                                return true;
                            }
                            default: return false;
                        }
                    }
                });
            }

            return popupMenu;
        }

        public void bind(UserNotification userNotification) {
            currentNotification = userNotification;
            binding.tvText.setText(userNotification.getText());
            binding.tvFireDate.setText(
                    userNotification.getFireDateTime().format(
                            DateTimeFormatter.ofLocalizedDateTime(
                                    FormatStyle.MEDIUM, FormatStyle.SHORT)
                    )
            );
        }

        public void setOnEditClick(OnNotificationClick onEditClick) {
            this.onEditClick = onEditClick;
        }


        public void setOnRemoveClick(OnNotificationClick onRemoveClick) {
            this.onRemoveClick = onRemoveClick;
        }
    }

}
