package com.example.prm392_gr5.Data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_gr5.Data.model.Notification;
import com.example.prm392_gr5.R;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> list) {
        this.notificationList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime;
        public ViewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.tvContent);
            tvTime = view.findViewById(R.id.tvTime);
        }
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification noti = notificationList.get(position);
        holder.tvContent.setText(noti.getContent());
        holder.tvTime.setText(noti.getTime());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}