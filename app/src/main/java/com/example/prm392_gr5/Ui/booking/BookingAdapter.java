package com.example.prm392_gr5.Ui.booking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392_gr5.Data.model.Booking;
import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.R;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {
    public interface OnCancelClick {
        void onCancel(int bookingId);
    }

    private final List<Booking> bookings;
    private final Map<Integer, Pitch> pitchMap;
    private final OnCancelClick listener;
    private final Context context;

    public BookingAdapter(Context ctx,
                          List<Booking> bookings,
                          List<Pitch> pitches,
                          OnCancelClick listener) {
        this.context  = ctx;
        this.bookings = bookings;
        this.listener = listener;
        this.pitchMap = new HashMap<>();
        for (Pitch p : pitches) {
            pitchMap.put(p.getId(), p);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Booking b = bookings.get(pos);
        Pitch p   = pitchMap.get(b.getPitchId());

        if (p != null) {
            // 1) Tên, địa chỉ, số điện thoại, giờ mở - đóng sân
            h.tvName.setText(p.getName());
            h.tvAddress.setText(p.getAddress());
            h.tvPhone.setText(p.getPhoneNumber());
            h.tvHours.setText(p.getOpenTime() + " - " + p.getCloseTime());

            // 2) Ngày chơi: parse JSON array từ b.getServices()
            String date = "";
            try {
                JSONArray arr = new JSONArray(b.getServices());
                if (arr.length() > 0) {
                    date = arr.getString(0);
                }
            } catch (Exception ignored) { }
            h.tvDate.setText(date);

            // 3) Khung giờ chơi: dùng luôn b.getDateTime()
            h.tvTimeSlot.setText(b.getDateTime());

            // 4) Load ảnh sân với Glide
            String url = p.getImageUrl();
            if (url != null && !url.isEmpty()) {
                if (url.startsWith("http")) {
                    Glide.with(context)
                            .load(url)
                            .placeholder(android.R.drawable.ic_menu_report_image)
                            .into(h.ivImage);
                } else {
                    int resId = context.getResources()
                            .getIdentifier(url, "drawable", context.getPackageName());
                    h.ivImage.setImageResource(
                            resId != 0 ? resId : android.R.drawable.ic_menu_report_image
                    );
                }
            } else {
                h.ivImage.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }

        // Hiển thị trạng thái
        String st = b.getStatus();
        String display;
        switch (st) {
            case "pending":   display = "Chưa thanh toán"; break;
            case "confirmed": display = "Đã thanh toán";       break;
            case "cancelled": display = "Đã huỷ";            break;
            default:          display = st;                 break;
        }
        h.tvStatus.setText(display);

        // Nút Hủy chỉ hiện khi status = "pending"
        h.btnCancel.setVisibility(
                "pending".equals(b.getStatus()) ? View.VISIBLE : View.GONE
        );
        h.btnCancel.setOnClickListener(v -> listener.onCancel(b.getId()));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvAddress, tvPhone, tvHours, tvDate, tvTimeSlot, tvStatus;
        Button btnCancel;

        VH(@NonNull View itemView) {
            super(itemView);
            ivImage    = itemView.findViewById(R.id.ivPitchImage);
            tvName     = itemView.findViewById(R.id.tvPitchName);
            tvAddress  = itemView.findViewById(R.id.tvPitchAddress);
            tvPhone    = itemView.findViewById(R.id.tvPhoneNumber);
            tvHours    = itemView.findViewById(R.id.tvOpeningHours);
            tvDate     = itemView.findViewById(R.id.tvDate);
            tvTimeSlot = itemView.findViewById(R.id.tvTimeSlot);
            tvStatus   = itemView.findViewById(R.id.tvStatus);
            btnCancel  = itemView.findViewById(R.id.btnCancel);
        }
    }
}