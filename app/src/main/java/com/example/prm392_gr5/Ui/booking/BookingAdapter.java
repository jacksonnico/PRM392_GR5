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
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {

    public interface OnCancelClick {
        void onCancel(int bookingId);
    }

    private final List<Booking> bookings;
    private final Map<Integer, Pitch> pitchMap;
    private final OnCancelClick listener;
    private final Context context;
    private final BookingRepository bookingRepo;

    private final SimpleDateFormat dbFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat dateOut =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeOut =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    public BookingAdapter(Context ctx,
                          List<Booking> bookings,
                          List<Pitch> pitches,
                          OnCancelClick listener) {
        this.context     = ctx;
        this.bookings    = bookings;
        this.listener    = listener;
        this.bookingRepo = new BookingRepository(ctx);
        this.pitchMap    = new HashMap<>();
        for (Pitch p : pitches) pitchMap.put(p.getId(), p);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Booking b = bookings.get(pos);
        Pitch p   = pitchMap.get(b.getPitchId());

        // Ảnh sân
        if (p != null && p.getImageUrl() != null && p.getImageUrl().startsWith("http")) {
            Glide.with(context)
                    .load(p.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .into(h.ivImage);
        } else {
            h.ivImage.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        // Tên + địa chỉ
        h.tvName.setText(p != null ? p.getName() : "–");
        h.tvAddress.setText(p != null ? p.getAddress() : "");

        // Ngày & giờ
        try {
            Date parsed = dbFormat.parse(b.getDateTime());
            SimpleDateFormat fullFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
            h.tvDateTime.setText(fullFormat.format(parsed));
        } catch (ParseException e) {
            h.tvDateTime.setText(b.getDateTime().replace("T", " "));
        }

// Khung giờ (TimeSlot)
        h.tvTimeslot.setText("Khung giờ: " + b.getTimeSlot());


        // Dịch vụ
        String svc = bookingRepo.getServiceText(b.getServices());
        h.tvServices.setText(svc.isEmpty() ? "Không có dịch vụ" : svc);



        // Trạng thái
        String st = b.getStatus();
        String disp;
        switch (st) {
            case "pending":   disp = "Chờ duyệt";     break;
            case "confirmed": disp = "Đã thanh toán"; break;
            case "cancelled": disp = "Đã huỷ";        break;
            default:          disp = st;              break;
        }
        h.tvStatus.setText(disp);

        // Nút Hủy chỉ với pending
        h.btnCancel.setVisibility("pending".equals(st) ? View.VISIBLE : View.GONE);
        h.btnCancel.setOnClickListener(v -> listener.onCancel(b.getId()));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvAddress, tvDateTime, tvServices, tvTimeslot, tvStatus;
        Button btnCancel;

        VH(@NonNull View v) {
            super(v);
            ivImage     = v.findViewById(R.id.ivPitchImage);
            tvName      = v.findViewById(R.id.tvPitchName);
            tvAddress   = v.findViewById(R.id.tvPitchAddress);
            tvDateTime  = v.findViewById(R.id.tvDateTime);
            tvServices  = v.findViewById(R.id.tvServices);
            tvTimeslot  = v.findViewById(R.id.tvTimeSlot);
            tvStatus    = v.findViewById(R.id.tvStatus);
            btnCancel   = v.findViewById(R.id.btnCancel);
        }
    }
}
