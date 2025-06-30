package com.example.prm392_gr5.Ui.admin.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Booking;
import com.example.prm392_gr5.R;

import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.ViewHolder> {
    private List<Booking> bookings;
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
        void onStatusChangeClick(Booking booking);
    }

    public BookingListAdapter(List<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    // Constructor cũ để tương thích
    public BookingListAdapter(List<Booking> bookings) {
        this.bookings = bookings;
        this.listener = null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvPitchName, tvUserId, tvDateTime, tvServices, tvDepositAmount, tvStatus;
        Button btnViewDetails, btnChangeStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvPitchName = itemView.findViewById(R.id.tvPitchName);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvServices = itemView.findViewById(R.id.tvServices);
            tvDepositAmount = itemView.findViewById(R.id.tvDepositAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnChangeStatus = itemView.findViewById(R.id.btnChangeStatus);
        }
    }

    @NonNull
    @Override
    public BookingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingListAdapter.ViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        holder.tvBookingId.setText("Booking #" + booking.getId());
        holder.tvPitchName.setText("Tên sân: " + booking.getPitchName());

        // Display user name instead of user ID
        String userDisplay = booking.getUserName() != null ? booking.getUserName() : "User #" + booking.getUserId();
        holder.tvUserId.setText("Người đặt: " + userDisplay);

        holder.tvDateTime.setText("Ngày giờ: " + booking.getDateTime());

        // Display service names instead of service IDs
        String servicesDisplay = getServicesDisplayText(booking);
        holder.tvServices.setText("Dịch vụ: " + servicesDisplay);

        holder.tvDepositAmount.setText("Cọc: " + String.format("%,.0fđ", booking.getDepositAmount()));

        // Set status với màu sắc tương ứng
        holder.tvStatus.setText(booking.getStatus());
        setStatusColor(holder.tvStatus, booking.getStatus());

        // Set click listeners
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onBookingClick(booking));

            holder.btnViewDetails.setOnClickListener(v -> listener.onBookingClick(booking));

            holder.btnChangeStatus.setOnClickListener(v -> listener.onStatusChangeClick(booking));
        }
    }

    private String getServicesDisplayText(Booking booking) {
        if (booking.getServiceNames() != null && !booking.getServiceNames().isEmpty()) {
            // Join service names with comma
            return String.join(", ", booking.getServiceNames());
        } else if (booking.getServices() != null && !booking.getServices().trim().isEmpty()) {
            // Fallback to service IDs if names are not available
            return booking.getServices();
        } else {
            return "Không có";
        }
    }

    private void setStatusColor(TextView tvStatus, String status) {
        switch (status.toLowerCase()) {
            case "đang chờ xác nhận":
            case "pending":
                tvStatus.setTextColor(Color.parseColor("#f39c12")); // Orange
                break;
            case "đã xác nhận":
            case "confirmed":
                tvStatus.setTextColor(Color.parseColor("#3498db")); // Blue
                break;
            case "hoàn thành":
            case "completed":
                tvStatus.setTextColor(Color.parseColor("#27ae60")); // Green
                break;
            case "đã hủy":
            case "cancelled":
                tvStatus.setTextColor(Color.parseColor("#e74c3c")); // Red
                break;
            default:
                tvStatus.setTextColor(Color.parseColor("#7f8c8d")); // Gray
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    // Method để cập nhật data
    public void updateData(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }
}