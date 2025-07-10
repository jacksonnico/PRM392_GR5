package com.example.prm392_gr5.Data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Notification;
import com.example.prm392_gr5.R;

import java.util.List;

public class OwnerNotificationAdapter extends RecyclerView.Adapter<OwnerNotificationAdapter.ViewHolder> {
    private final List<Notification> notifications;

    public OwnerNotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đảm bảo dùng đúng layout chứa tvContent, tvTime, tvUserName
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        // Gán dữ liệu vào TextView
        holder.tvContent.setText(notification.getContent());
        holder.tvTime.setText(notification.getTime());

        if (notification.getUserName() != null && !notification.getUserName().isEmpty()) {
            holder.tvUserName.setVisibility(View.VISIBLE);
            holder.tvUserName.setText("Người đặt: " + notification.getUserName());
        } else {
            holder.tvUserName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime, tvUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }
    }
}
