package com.example.prm392_gr5.Data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Data.model.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSender, tvMessage, tvTime;

        public MessageViewHolder(View view) {
            super(view);
            tvSender = view.findViewById(R.id.tvSender);
            tvMessage = view.findViewById(R.id.tvMessage);
            tvTime = view.findViewById(R.id.tvTime);
        }
    }

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvSender.setText(message.getSender());
        holder.tvMessage.setText(message.getMessage());
        holder.tvTime.setText(message.getTime());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}