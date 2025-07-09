package com.example.prm392_gr5.Ui.booking;

import android.content.Context;
import android.util.Log;
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

            // Sửa phần này để xử lý cột services
            String servicesData = b.getServices();
            String dateToDisplay = ""; // Đây sẽ là chuỗi ngày hiển thị

            if (servicesData != null && !servicesData.isEmpty()) {
                try {
                    // Thử parse như JSON Array (dành cho [1,2], [3]...)
                    JSONArray arr = new JSONArray(servicesData);
                    if (arr.length() > 0) {
                        // Nếu là JSON array, lấy phần tử đầu tiên (có thể là ID dịch vụ)
                        // Hoặc bạn có thể format lại để hiển thị các dịch vụ
                        // Ví dụ: dateToDisplay = "Dịch vụ: " + arr.getString(0) + "...";
                        // Tạm thời, nếu bạn muốn nó hiển thị ngày như trước thì chúng ta phải check kĩ hơn.
                        // NHƯNG, CỘT SERVICES KHÔNG NÊN LƯU NGÀY.
                        // Nếu nó là JSON Array thì nó không phải là ngày.
                        // Để giữ nguyên logic cũ là lấy ngày từ services nếu có,
                        // thì phải có một cách phân biệt rõ ràng.

                        // Giả định nếu là JSON Array thì không phải là ngày để hiển thị ở tvDate
                        // và bạn sẽ không hiển thị gì cho tvDate nếu đó là array dịch vụ.
                        dateToDisplay = ""; // Nếu là dịch vụ, không hiển thị ngày ở đây.
                        // Hoặc bạn có thể tạo một TextView khác để hiển thị dịch vụ.

                        // Log để debug xem nó có parse được JSON không
                        Log.d("BookingAdapter", "Parsed services as JSON Array: " + servicesData);
                    }
                } catch (Exception e) {
                    // Nếu không phải JSON Array, thử coi nó là một chuỗi ngày
                    // Ví dụ: "07/07", "08/07"
                    // Log để debug xem nó có vào đây không
                    Log.d("BookingAdapter", "Services is not JSON Array, treating as date string: " + servicesData);
                    dateToDisplay = servicesData; // Lấy trực tiếp chuỗi "07/07" hoặc "08/07"
                }
            }
            h.tvDate.setText(dateToDisplay);


            // 3) Khung giờ chơi: dùng luôn b.getDateTime()
            // Chúng ta đã thống nhất DateTime trong DB nên là yyyy-MM-ddTHH:mm:ss
            // Vì vậy, chỉ lấy phần giờ:phút từ chuỗi DateTime
            String timeSlotToDisplay = "";
            String fullDateTime = b.getDateTime();
            if (fullDateTime != null && fullDateTime.length() >= 16) { // "yyyy-MM-ddTHH:mm:ss"
                try {
                    // Sử dụng SimpleDateFormat để đảm bảo an toàn hơn và xử lý các trường hợp khác
                    // hơn là substring cứng
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    Date dateObj = inputFormat.parse(fullDateTime);
                    timeSlotToDisplay = outputTimeFormat.format(dateObj);
                } catch (ParseException e) {
                    Log.e("BookingAdapter", "Error parsing fullDateTime for time slot: " + fullDateTime, e);
                    // Nếu parse lỗi, có thể do định dạng cũ "HH:mm-HH:mm" vẫn còn trong DB
                    // Thử xử lý định dạng cũ này nếu bạn chưa sửa DB hoàn toàn
                    if (fullDateTime.contains("-")) {
                        timeSlotToDisplay = fullDateTime.split("-")[0]; // Lấy giờ bắt đầu
                    } else {
                        timeSlotToDisplay = "Giờ không hợp lệ"; // Fallback nếu không xác định được
                    }
                }
            } else {
                // Trường hợp fullDateTime null hoặc quá ngắn
                if (fullDateTime != null && fullDateTime.contains("-")) { // Kiểm tra nếu là định dạng "HH:mm-HH:mm"
                    timeSlotToDisplay = fullDateTime.split("-")[0];
                } else {
                    timeSlotToDisplay = "Không rõ giờ";
                }
            }
            h.tvTimeSlot.setText(timeSlotToDisplay);


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
            case "pending": display = "Chờ duyệt"; break;
            case "confirmed": display = "Đã thanh toán"; break;
            case "cancelled": display = "Đã huỷ"; break;
            case "approved": display = "Đã duyệt"; break;
            case "rejected": display = "Đã từ chối"; break;
            default: display = st; break;
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