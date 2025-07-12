package com.example.prm392_gr5.Data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.R;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private int currentUserId; // ✅ Thêm để phân biệt user hiện tại
    private int ownerId; // ✅ Thêm để phân biệt owner

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSenderName, tvMessage, tvTime;

        public MessageViewHolder(View view) {
            super(view);
            tvSenderName = view.findViewById(R.id.tvSenderName);
            tvMessage = view.findViewById(R.id.tvMessage);
            tvTime = view.findViewById(R.id.tvTime);
        }
    }

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
        this.currentUserId = 0; // Default
        this.ownerId = 0; // Default
    }

    // ✅ Constructor với thông tin user và owner
    public MessageAdapter(List<Message> messages, int currentUserId, int ownerId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.ownerId = ownerId;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_owner, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        // ✅ Xử lý hiển thị tên sender
        String displayName = getDisplayName(message);
        holder.tvSenderName.setText(displayName);
        holder.tvMessage.setText(message.getMessage());
        holder.tvTime.setText(message.getTime());

        // ✅ Styling khác nhau cho từng loại message
        if (isOwnerMessage(message)) {
            // Owner message - màu xanh dương
            holder.tvSenderName.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
        } else if (isUserMessage(message)) {
            // User message - màu xanh lá
            holder.tvSenderName.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
        } else {
            // System message - màu xám
            holder.tvSenderName.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
        }
    }

    private String getDisplayName(Message message) {
        String sender = message.getSender();

        // ✅ Nếu sender đã có tên cụ thể, dùng luôn
        if (sender != null && !sender.isEmpty()) {
            return sender;
        }

        // ✅ Fallback dựa trên userId
        if (message.getUserId() == 0) {
            return "Hệ thống";
        } else if (message.getUserId() == currentUserId) {
            return "Bạn";
        } else {
            return "Khách hàng";
        }
    }

    private boolean isOwnerMessage(Message message) {
        // ✅ Owner message: sender không phải là "Khách hàng" hoặc "Hệ thống"
        String sender = message.getSender();
        return sender != null && !sender.equals("Khách hàng") && !sender.equals("Hệ thống") && !sender.equals("Bạn");
    }

    private boolean isUserMessage(Message message) {
        // ✅ User message: sender là "Khách hàng" hoặc userId > 0
        String sender = message.getSender();
        return sender != null && (sender.equals("Khách hàng") || sender.equals("Bạn")) && message.getUserId() > 0;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ✅ Method để cập nhật danh sách messages
    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }
}