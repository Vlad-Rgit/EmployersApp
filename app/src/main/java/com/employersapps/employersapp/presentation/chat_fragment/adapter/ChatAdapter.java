package com.employersapps.employersapp.presentation.chat_fragment.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.employersapps.core.domain.Message;
import com.employersapps.core.domain.MessageAttachment;
import com.employersapps.employersapp.R;
import com.employersapps.employersapp.databinding.ItemChatMessageBinding;
import com.employersapps.employersapp.databinding.ItemNewsFileListBinding;
import com.employersapps.employersapp.framework.util.PaddingDecorator;
import com.employersapps.employersapp.framework.util.SimpleDiffCallback;
import com.employersapps.employersapp.presentation.chat_fragment.state.ReceiverState;
import com.employersapps.employersapp.presentation.messages_fragment.adapter.MessagesAdapter;
import com.employersapps.employersapp.presentation.news_details_fragment.intents.AddMultipleImageIntent;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    public interface OnAttachmentClickedListener {
        void onAttachmentClicked(MessageAttachment messageAttachment);
    }

    public interface OnMessageClickedListener {
        void onMessageClicked(Message message);
    }

    private List<Message> items = Collections.emptyList();
    private long currentUserId;

    private OnAttachmentClickedListener onAttachmentClickedListener;
    private OnMessageClickedListener onMarkClicked;
    private boolean isPrivateChat = true;


    public void setPrivateChat(boolean privateChat) {
        isPrivateChat = privateChat;
    }

    public void setOnAttachmentClickedListener(OnAttachmentClickedListener onAttachmentClickedListener) {
        this.onAttachmentClickedListener = onAttachmentClickedListener;
    }

    public void setOnMarkClicked(OnMessageClickedListener onMarkClicked) {
        this.onMarkClicked = onMarkClicked;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatMessageBinding binding = ItemChatMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        MessageViewHolder holder = new MessageViewHolder(binding, currentUserId);

        holder.setOnAttachmentClickedListener(new OnAttachmentClickedListener() {
            @Override
            public void onAttachmentClicked(MessageAttachment messageAttachment) {
                if(onAttachmentClickedListener  != null) {
                    onAttachmentClickedListener.onAttachmentClicked(messageAttachment);
                }
            }
        });

        holder.setOnMarkClicked(new OnMessageClickedListener() {
            @Override
            public void onMessageClicked(Message message) {
                if(onMarkClicked != null) {
                    onMarkClicked.onMessageClicked(message);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(items.get(position), isPrivateChat);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Message> newItems) {
        SimpleDiffCallback<Message> diffCallback = new SimpleDiffCallback<>(
                items, newItems
        );
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    public void setCurrentUserId(long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatMessageBinding binding;
        private final long currentUserId;
        private OnMessageClickedListener onMarkClicked;
        private Message currentMessage;
        private OnAttachmentClickedListener onAttachmentClickedListener;

        public void setOnAttachmentClickedListener(OnAttachmentClickedListener onAttachmentClickedListener) {
            this.onAttachmentClickedListener = onAttachmentClickedListener;
        }

        public MessageViewHolder(ItemChatMessageBinding binding, long currentUserId) {
            super(binding.getRoot());
            this.binding = binding;
            this.currentUserId = currentUserId;

            binding.messageHost.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu menu = new PopupMenu(
                            v.getContext(),
                            v
                    );

                    menu.inflate(R.menu.menu_message);


                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if(item.getItemId() == R.id.menu_mark_message &&
                                onMarkClicked != null) {
                                onMarkClicked.onMessageClicked(currentMessage);
                                return true;
                            }

                            return false;

                        }
                    });

                    menu.show();

                    return false;
                }
            });

            binding.rvAttachments.setHasFixedSize(true);
            binding.rvAttachments.setLayoutManager(new LinearLayoutManager(
                    binding.getRoot().getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            ));

            int padding = binding.getRoot().getContext()
                    .getResources()
                    .getDimensionPixelSize(R.dimen.default_padding) / 2;

            binding.rvAttachments.addItemDecoration(new PaddingDecorator(0, 0, padding, 0));
        }

        public void setOnMarkClicked(OnMessageClickedListener onMarkClicked) {
            this.onMarkClicked = onMarkClicked;
        }

        private DateTimeFormatter getBestDateFormatter(LocalDateTime timestamp) {

            LocalDate date = timestamp.toLocalDate();
            LocalDate now = LocalDate.now();

            if(date.equals(now)) {
                return DateTimeFormatter.ofLocalizedTime(
                        FormatStyle.SHORT
                );
            }
            else {
                return DateTimeFormatter.ofLocalizedDate(
                        FormatStyle.SHORT);
            }
        }

        public void bind(Message message, boolean isPrivateChat) {

            currentMessage = message;

            binding.tvText.setText(message.getText());

            LocalDateTime timestamp = message.getTimestamp();
            LocalDateTime firstReadTimestamp = message.getFirstReadTimestamp();


            binding.tvTimestamp.setText(timestamp.format(
                    getBestDateFormatter(timestamp)
            ));

            if (firstReadTimestamp == null) {
               binding.tvFirstReadTimestamp.setVisibility(View.GONE);
            }
            else {
                binding.tvFirstReadTimestamp.setText(
                        firstReadTimestamp.format(
                                getBestDateFormatter(firstReadTimestamp)
                        )
                );
            }

            if(isPrivateChat) {
                binding.tvSenderName.setVisibility(View.GONE);
            }
            else if (message.getSender() != null) {
                binding.tvSenderName.setText(message.getSender().getFullName());
            }

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                    binding.messageHost.getLayoutParams();

            if (message.getSenderId() == currentUserId) {
                params.gravity = Gravity.END;
            } else {
                params.gravity = Gravity.START;
            }

            if (message.isSending()) {
                binding.tvSending.setVisibility(View.VISIBLE);
            } else {
                binding.tvSending.setVisibility(View.GONE);
            }

            binding.messageHost.setLayoutParams(params);

            if (message.getMessageAttachments().size() > 0) {
                binding.rvAttachments.setVisibility(View.VISIBLE);
                MessageAttachmentAdapter adapter = new MessageAttachmentAdapter(
                        message.getMessageAttachments()
                );
                adapter.setOnAttachmentClickedListener(new OnAttachmentClickedListener() {
                    @Override
                    public void onAttachmentClicked(MessageAttachment messageAttachment) {
                        if (onAttachmentClickedListener != null) {
                            onAttachmentClickedListener.onAttachmentClicked(messageAttachment);
                        }
                    }
                });
                binding.rvAttachments.setAdapter(adapter);
            } else {
                binding.rvAttachments.setVisibility(View.GONE);
            }

        }


        public static class MessageAttachmentAdapter extends RecyclerView.Adapter<MessageAttachmentAdapter.MessageAttachmentViewHolder>{

            private final List<MessageAttachment> attachments;

            private OnAttachmentClickedListener onAttachmentClickedListener;

            public void setOnAttachmentClickedListener(OnAttachmentClickedListener onAttachmentClickedListener) {
                this.onAttachmentClickedListener = onAttachmentClickedListener;
            }

            public MessageAttachmentAdapter(List<MessageAttachment> attachments) {
                this.attachments = attachments;
            }

            @NonNull
            @Override
            public MessageAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                MessageAttachmentViewHolder holder = new MessageAttachmentViewHolder(
                        ItemNewsFileListBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );

                holder.setOnAttachmentClickedListener(new OnAttachmentClickedListener() {
                    @Override
                    public void onAttachmentClicked(MessageAttachment messageAttachment) {
                        if(onAttachmentClickedListener  != null) {
                            onAttachmentClickedListener.onAttachmentClicked(messageAttachment);
                        }
                    }
                });

                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull MessageAttachmentViewHolder holder, int position) {
                holder.bind(attachments.get(position));
            }

            @Override
            public int getItemCount() {
                return attachments.size();
            }

            public static class MessageAttachmentViewHolder extends RecyclerView.ViewHolder {

                private final ItemNewsFileListBinding binding;

                private OnAttachmentClickedListener onAttachmentClickedListener;
                private MessageAttachment currentItem;

                public void setOnAttachmentClickedListener(OnAttachmentClickedListener onAttachmentClickedListener) {
                    this.onAttachmentClickedListener = onAttachmentClickedListener;
                }

                public MessageAttachmentViewHolder(ItemNewsFileListBinding binding) {
                    super(binding.getRoot());
                    this.binding = binding;
                    binding.getRoot().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(currentItem != null && onAttachmentClickedListener != null) {
                                onAttachmentClickedListener.onAttachmentClicked(currentItem);
                            }
                        }
                    });
                }

                public void bind(MessageAttachment attachment) {
                    currentItem = attachment;
                    binding.tvFileName.setText(attachment.getShortName());
                }
            }
        }
    }
}
