package com.employersapps.employersapp.presentation.employers_fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.User;
import com.employersapps.employersapp.EmployersApp;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.config.Config;
import com.employersapps.employersapp.databinding.EmployerItemBinding;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class EmployerAdapter extends RecyclerView.Adapter<EmployerAdapter.EmployerViewHolder>{

    public interface OnEmployerClickListener {
        void onEmployerClicked(Employer employer);
    }

    private final User currentUser;

    public EmployerAdapter(Context context) {
        EmployersApp employersApp = (EmployersApp) context.getApplicationContext();
        currentUser = employersApp.getAppComponent()
                .getUserDataSource()
                .getCurrentUser();
    }



    private OnEmployerClickListener onEmployerClickListener;
    private OnEmployerClickListener onRemoveClickedListener;
    private OnEmployerClickListener onEditClickedListener;
    private OnEmployerClickListener onSendNotificationClickedListener;

    private List<Employer> items = Collections.emptyList();

    @NonNull
    @Override
    public EmployerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EmployerItemBinding binding = EmployerItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        EmployerViewHolder holder = new EmployerViewHolder(binding, currentUser);

        holder.setOnEmployerClickListener(new OnEmployerClickListener() {
            @Override
            public void onEmployerClicked(Employer employer) {
                if(onEmployerClickListener != null) {
                    onEmployerClickListener.onEmployerClicked(employer);
                }
            }
        });

        holder.setOnEditClickedListener(new OnEmployerClickListener() {
            @Override
            public void onEmployerClicked(Employer employer) {
                if(onEditClickedListener != null) {
                    onEditClickedListener.onEmployerClicked(employer);
                }
            }
        });

        holder.setOnRemoveClickedListener(new OnEmployerClickListener() {
            @Override
            public void onEmployerClicked(Employer employer) {
                if(onRemoveClickedListener != null) {
                    onRemoveClickedListener.onEmployerClicked(employer);
                }
            }
        });

        holder.setOnSendNotificationClickedListener(new OnEmployerClickListener() {
            @Override
            public void onEmployerClicked(Employer employer) {
                if(onSendNotificationClickedListener != null) {
                    onSendNotificationClickedListener.onEmployerClicked(employer);
                }
            }
        });

        return holder;
    }

    public void setOnEditClickedListener(OnEmployerClickListener onEditClickedListener) {
        this.onEditClickedListener = onEditClickedListener;
    }

    public void setOnSendNotificationClickedListener(OnEmployerClickListener onSendNotificationClickedListener) {
        this.onSendNotificationClickedListener = onSendNotificationClickedListener;
    }

    @Override
    public void onBindViewHolder(@NonNull EmployerViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    public void setOnRemoveClickedListener(OnEmployerClickListener onRemoveClickedListener) {
        this.onRemoveClickedListener = onRemoveClickedListener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnEmployerClickListener(OnEmployerClickListener onEmployerClickListener) {
        this.onEmployerClickListener = onEmployerClickListener;
    }

    public void setItems(List<Employer> newItems) {
        SimpleDiffCallback<Employer> diffCallback =
                new SimpleDiffCallback<>(items, newItems);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    public static class EmployerViewHolder extends RecyclerView.ViewHolder {

        private OnEmployerClickListener onEmployerClickListener;
        private OnEmployerClickListener onEditClickedListener;
        private OnEmployerClickListener onRemoveClickedListener;
        private OnEmployerClickListener onSendNotificationClickedListener;


        private final EmployerItemBinding binding;
        private Employer currentEmployer;
        private final int cornerRadius;
        private final User currentUser;

        public EmployerViewHolder(EmployerItemBinding binding, User currentUser) {
            super(binding.getRoot());
            this.binding = binding;
            this.currentUser = currentUser;

            cornerRadius = binding.getRoot().getContext()
                    .getResources()
                    .getDimensionPixelSize(R.dimen.corner_radius);

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentEmployer != null &&
                            onEmployerClickListener != null) {
                        onEmployerClickListener.onEmployerClicked(currentEmployer);
                    }
                }
            });

            binding.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    PopupMenu popupMenu = new PopupMenu(binding.getRoot().getContext(),
                            binding.btnMenu);

                    popupMenu.inflate(R.menu.item_employer_menu);

                    if(currentUser.getId() == currentEmployer.getId()) {
                        popupMenu.getMenu().findItem(R.id.menu_delete)
                                .setVisible(false);
                    }




                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.menu_edit: {
                                    if(onEditClickedListener != null) {
                                        onEditClickedListener.onEmployerClicked(currentEmployer);
                                    }
                                    return true;
                                }
                                case R.id.menu_delete: {
                                    if(onRemoveClickedListener != null) {
                                        onRemoveClickedListener.onEmployerClicked(currentEmployer);
                                    }
                                    return true;
                                }
                                case R.id.send_notification: {
                                    if(onSendNotificationClickedListener != null) {
                                        onSendNotificationClickedListener.onEmployerClicked(currentEmployer);
                                    }
                                }
                            }

                            return false;
                        }
                    });

                    popupMenu.show();
                }
            });
        }

        public void setOnSendNotificationClickedListener(OnEmployerClickListener onSendNotificationClickedListener) {
            this.onSendNotificationClickedListener = onSendNotificationClickedListener;
        }

        public void setOnRemoveClickedListener(OnEmployerClickListener onRemoveClickedListener) {
            this.onRemoveClickedListener = onRemoveClickedListener;
        }

        public void setOnEditClickedListener(OnEmployerClickListener onEditClickedListener) {
            this.onEditClickedListener = onEditClickedListener;
        }

        public void bind(Employer employer) {
            currentEmployer = employer;

            if(employer.getStartVacationDate() == null ||
                employer.getEndVacationDate() == null) {
                binding.tvAtVacation.setVisibility(View.GONE);
            }
            else if(employer.getEndVacationDate().isAfter(LocalDate.now())) {
                binding.tvAtVacation.setVisibility(View.VISIBLE);
            }
            else {
                binding.tvAtVacation.setVisibility(View.GONE);
            }

            Glide.with(binding.img)
                    .load(Config.API_ADDRESS + "/static/employers/" + employer.getPhotoPath())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .into(binding.img);

            binding.tvFullName.setText(currentEmployer.getShortFullName());
            binding.tvPost.setText(currentEmployer.getPost().getName());

            if(currentUser.getRoleId() != 1) {
                binding.btnMenu.setVisibility(View.GONE);
            }
        }

        public void setOnEmployerClickListener(OnEmployerClickListener onEmployerClickListener) {
            this.onEmployerClickListener = onEmployerClickListener;
        }
    }

}
