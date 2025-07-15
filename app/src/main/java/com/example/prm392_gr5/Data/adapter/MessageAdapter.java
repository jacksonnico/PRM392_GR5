package com.example.prm392_gr5.Data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private final int currentUserId; // Người dùng đăng nhập (0 nếu chủ sân)
    private final int ownerId;       // Chủ sân ID

    public MessageAdapter(List<Message> messages, int currentUserId, int ownerId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.ownerId = ownerId;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        boolean isSentByCurrentUser;
        if (currentUserId > 0) {
            // Người dùng đăng nhập
            isSentByCurrentUser = message.getUserId() == currentUserId;
        } else {
            // Chủ sân đăng nhập
            isSentByCurrentUser = message.getOwnerId() == ownerId;
        }

        if (isSentByCurrentUser) {
            holder.sentMessageLayout.setVisibility(View.VISIBLE);
            holder.receivedMessageLayout.setVisibility(View.GONE);
            holder.tvSenderNameSent.setText(message.getSender());
            holder.tvMessageSent.setText(message.getMessage());
            holder.tvTimeSent.setText(message.getTime());
        } else {
            holder.sentMessageLayout.setVisibility(View.GONE);
            holder.receivedMessageLayout.setVisibility(View.VISIBLE);
            holder.tvSenderNameReceived.setText(message.getSender());
            holder.tvMessageReceived.setText(message.getMessage());
            holder.tvTimeReceived.setText(message.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sentMessageLayout, receivedMessageLayout;
        TextView tvSenderNameSent, tvMessageSent, tvTimeSent;
        TextView tvSenderNameReceived, tvMessageReceived, tvTimeReceived;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessageLayout = itemView.findViewById(R.id.sentMessageLayout);
            receivedMessageLayout = itemView.findViewById(R.id.receivedMessageLayout);
            tvSenderNameSent = itemView.findViewById(R.id.tvSenderNameSent);
            tvMessageSent = itemView.findViewById(R.id.tvMessageSent);
            tvTimeSent = itemView.findViewById(R.id.tvTimeSent);
            tvSenderNameReceived = itemView.findViewById(R.id.tvSenderNameReceived);
            tvMessageReceived = itemView.findViewById(R.id.tvMessageReceived);
            tvTimeReceived = itemView.findViewById(R.id.tvTimeReceived);
        }
    }
}
