package com.example.prm392_gr5.Ui.owner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.ScheduleInfo;
import com.example.prm392_gr5.R;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<ScheduleInfo> schedules;
    private Context context;

    public ScheduleAdapter(Context context, List<ScheduleInfo> schedules) {
        this.context = context;
        this.schedules = schedules;
    }

    public void setScheduleList(List<ScheduleInfo> newList) {
        this. schedules = newList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduleInfo schedule = schedules.get(position);
        holder.tvTimeSlot.setText(schedule.timeSlot);

        if (schedule.isBooked) {
            holder.tvStatus.setText(getStatusText(schedule.status));
            holder.tvStatus.setTextColor(getStatusColor(schedule.status));
            holder.tvCustomerName.setText(schedule.customerName);
            holder.tvCustomerPhone.setText(schedule.customerPhone);
            holder.tvCustomerName.setVisibility(View.VISIBLE);
            holder.tvCustomerPhone.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_bright));
        } else {
            holder.tvStatus.setText("Trống");
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.tvCustomerName.setVisibility(View.GONE);
            holder.tvCustomerPhone.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chờ duyệt";
            case "approved": return "Đã duyệt";
            case "rejected": return "Đã từ chối";
            case "confirmed": return "Đã thanh toán";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "pending": return context.getResources().getColor(android.R.color.holo_orange_dark);
            case "approved": return context.getResources().getColor(android.R.color.holo_green_dark);
            case "rejected":
            case "cancelled": return context.getResources().getColor(android.R.color.holo_red_dark);
            case "completed": return context.getResources().getColor(android.R.color.holo_blue_dark);
            default: return context.getResources().getColor(android.R.color.black);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeSlot, tvStatus, tvCustomerName, tvCustomerPhone;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvCustomerPhone = itemView.findViewById(R.id.tv_customer_phone);
        }
    }

}