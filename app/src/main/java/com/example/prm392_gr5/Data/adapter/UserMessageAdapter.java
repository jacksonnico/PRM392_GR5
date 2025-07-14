package com.example.prm392_gr5.Data.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.UserMessageSummary;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.PitchOwnerMessagesActivity;

import java.util.List;

public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.UserMessageViewHolder> {
    private List<UserMessageSummary> userMessages;
    private int ownerId;
    private int userId; // ✅ Thêm userId vào Adapter

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDisplayName, tvLastMessage, tvTime;

        public UserMessageViewHolder(View view) {
            super(view);
            tvDisplayName = view.findViewById(R.id.tvUserName);
            tvLastMessage = view.findViewById(R.id.tvLastMessage);
            tvTime = view.findViewById(R.id.tvTime);
        }
    }

    // ✅ Constructor mới nhận 3 tham số
    public UserMessageAdapter(List<UserMessageSummary> userMessages, int ownerId, int userId) {
        this.userMessages = userMessages;
        this.ownerId = ownerId;
        this.userId = userId;
    }

    @Override
    public UserMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
        return new UserMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserMessageViewHolder holder, int position) {
        UserMessageSummary summary = userMessages.get(position);
        holder.tvDisplayName.setText(summary.getDisplayName());
        holder.tvLastMessage.setText(summary.getLastMessage());
        holder.tvTime.setText(summary.getLastTime());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PitchOwnerMessagesActivity.class);
            intent.putExtra("pitchName", summary.getPitchName());
            intent.putExtra("fullName", summary.getDisplayName());
            intent.putExtra("phoneNumber", summary.getPhoneNumber());
            intent.putExtra("userId", summary.getUserId()); // ✅ Lấy userId từ summary
            intent.putExtra("ownerId", ownerId);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userMessages.size();
    }
}
