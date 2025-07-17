package com.example.prm392_gr5.Ui.owner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.BookingInfo;
import com.example.prm392_gr5.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    private List<BookingInfo> bookings;
    private OnBookingActionListener listener;
    public interface OnBookingStatusChangedListener {
        void onStatusChanged();
    }

    public interface OnBookingActionListener {
        void onApproveClick(int bookingId);
        void onRejectClick(int bookingId);
        void onBookingClick(BookingInfo booking);
    }

    public BookingAdapter(List<BookingInfo> bookings, OnBookingActionListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_approve, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookingInfo booking = bookings.get(position);

        // Debug logs
        System.out.println("=== Binding booking " + position + " ===");
        System.out.println("Booking ID: " + booking.id);
        System.out.println("User Name: " + booking.userName);
        System.out.println("Pitch Name: " + booking.pitchName);
        System.out.println("Raw DateTime: " + booking.dateTime);
//        System.out.println("Deposit: " + booking.depositAmount);

        holder.tvUserName.setText(booking.userName);
        holder.tvPitchName.setText(booking.pitchName);

        String formattedDateTime = formatDateTime(booking.dateTime);
        System.out.println("Final formatted: " + formattedDateTime);

        holder.tvDateTime.setText(formattedDateTime);
//        holder.tvDeposit.setText(String.format("%.0f VNĐ", booking.depositAmount));

        // Set click listeners
        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApproveClick(booking.id);
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRejectClick(booking.id);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookingClick(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    private String formatDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "Chưa có thời gian";
        }

        try {
            // Debug log
            System.out.println("Input dateTime: " + dateTime);

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            Date date = inputFormat.parse(dateTime);
            if (date != null) {
                String result = outputFormat.format(date);
                System.out.println("Formatted result: " + result);
                return result;
            } else {
                System.out.println("Parse returned null");
                return dateTime;
            }

        } catch (Exception e) {
            System.out.println("Parse error: " + e.getMessage());
            e.printStackTrace();
            return dateTime;
        }
    }

    public void updateBookings(List<BookingInfo> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvPitchName, tvDateTime, tvDeposit;
        Button btnApprove, btnReject;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvPitchName = itemView.findViewById(R.id.tv_pitch_name);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvDeposit = itemView.findViewById(R.id.tv_deposit);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}