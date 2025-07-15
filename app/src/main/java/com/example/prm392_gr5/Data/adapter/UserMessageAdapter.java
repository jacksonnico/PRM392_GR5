package com.example.prm392_gr5.Data.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.UserMessageSummary;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.PitchOwnerMessagesActivity;

import java.util.List;

public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.ViewHolder> {
    private final List<UserMessageSummary> messageSummaries;
    private final int ownerId;
    private final Context context;

    public UserMessageAdapter(List<UserMessageSummary> messageSummaries, int ownerId, Context context) {
        this.messageSummaries = messageSummaries;
        this.ownerId = ownerId;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserMessageSummary summary = messageSummaries.get(position);

        holder.tvUserName.setText(summary.getUserName() != null ? summary.getUserName() : "Người dùng");
        holder.tvPitchName.setText(summary.getPitchName() != null ? summary.getPitchName() : "Sân bóng");
        holder.tvLastMessage.setText(summary.getLastMessage() != null ? summary.getLastMessage() : "(Chưa có tin nhắn)");
        holder.tvTime.setText(summary.getLastTime() != null ? summary.getLastTime() : ""); // ✅ FIX

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PitchOwnerMessagesActivity.class);
            intent.putExtra("pitchName", summary.getPitchName());
            intent.putExtra("phoneNumber", summary.getPhoneNumber());
            intent.putExtra("userId", summary.getUserId());
            intent.putExtra("userName", summary.getUserName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messageSummaries != null ? messageSummaries.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvPitchName, tvLastMessage, tvTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvPitchName = itemView.findViewById(R.id.tvPitchName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
