package com.example.prm392_gr5.Ui.owner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.ScheduleInfo;
import com.example.prm392_gr5.R;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private Context context;
    private List<ScheduleInfo> scheduleList;
    private List<String> timeSlots;
    private Calendar selectedDateToDisplay;

    public ScheduleAdapter(Context context, List<ScheduleInfo> scheduleList, List<String> timeSlots, Calendar selectedDateToDisplay) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.timeSlots = timeSlots;
        this.selectedDateToDisplay = selectedDateToDisplay;
    }

    // Phương thức để cập nhật dữ liệu khi người dùng chọn ngày khác
    public void setScheduleData(List<ScheduleInfo> newScheduleList, Calendar newSelectedDateToDisplay) {
        this.scheduleList.clear();
        if (newScheduleList != null) {
            this.scheduleList.addAll(newScheduleList);
        }
        this.selectedDateToDisplay = newSelectedDateToDisplay;
        notifyDataSetChanged(); // Yêu cầu RecyclerView vẽ lại toàn bộ
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentTimeSlot = timeSlots.get(position);
        holder.tvTimeSlot.setText(currentTimeSlot);

        // Không cần resetCells() nữa vì giờ chỉ có một ô ngày duy nhất trên mỗi hàng
        // Kiểm tra xem selectedDateToDisplay có hợp lệ không
        if (selectedDateToDisplay == null) {
            // Nếu không có ngày nào được chọn, hiển thị ô trống hoặc ẩn đi
            holder.llBookedContent.setVisibility(View.GONE);
            holder.tvAvailable.setVisibility(View.VISIBLE);
            holder.tvAvailable.setText("N/A"); // Hoặc một thông báo khác
            holder.flSelectedDayContent.setBackgroundResource(R.drawable.cell_background);
            holder.flSelectedDayContent.setOnClickListener(null); // Xóa listener
            return;
        }

        // Tìm kiếm thông tin lịch đặt cho khung giờ hiện tại và ngày đã chọn
        ScheduleInfo bookedSchedule = findScheduleForSlotAndDay(currentTimeSlot, selectedDateToDisplay);

        if (bookedSchedule != null && "approved".equalsIgnoreCase(bookedSchedule.getStatus())) { // 1 nghĩa là đã đặt
            holder.llBookedContent.setVisibility(View.VISIBLE);
            holder.tvAvailable.setVisibility(View.GONE);
            holder.flSelectedDayContent.setBackgroundResource(R.drawable.cell_booked_background);

            holder.tvStatus.setText("Đã đặt");
            holder.tvCustomerName.setText("KH: " + (bookedSchedule.getCustomerName() != null ? bookedSchedule.getCustomerName() : "N/A"));
            holder.tvCustomerPhone.setText("SĐT: " + (bookedSchedule.getCustomerPhone() != null ? bookedSchedule.getCustomerPhone() : "N/A"));

            holder.flSelectedDayContent.setOnClickListener(v -> {
                Log.d("ScheduleAdapter", "Booked slot clicked: " + bookedSchedule.getTimeSlot() + " on " + bookedSchedule.getBookingDate());
                // Xử lý sự kiện click vào ô đã đặt (ví dụ: mở chi tiết đặt lịch)
            });

        } else {
            holder.llBookedContent.setVisibility(View.GONE);
            holder.tvAvailable.setVisibility(View.VISIBLE);
            holder.flSelectedDayContent.setBackgroundResource(R.drawable.cell_background);

            holder.tvAvailable.setText("Trống");

            // Lấy thời gian bắt đầu của khung giờ để truyền khi đặt lịch mới
            String startTimeOnly = currentTimeSlot.split("-")[0]; // "06:00-08:00" -> "06:00"
            // Lấy ngày chính xác cho ô này để truyền khi đặt lịch mới
            final long bookingDateMillis = selectedDateToDisplay.getTimeInMillis();

            holder.flSelectedDayContent.setOnClickListener(v -> {
                Log.d("ScheduleAdapter", "Available slot clicked: " + currentTimeSlot + " on " + selectedDateToDisplay.getTime());
            });
        }
    }

    // Phương thức tìm lịch đặt vẫn giữ nguyên vì logic so sánh ngày và khung giờ vẫn đúng
    private ScheduleInfo findScheduleForSlotAndDay(String timeSlotInRow, Calendar dayCalendar) {
        if (scheduleList == null) return null;

        for (ScheduleInfo schedule : scheduleList) {
            if (schedule == null || schedule.getBookingDate() == null || schedule.getTimeSlot() == null) {
                continue; // Bỏ qua các mục lịch không hợp lệ
            }

            // So sánh ngày (năm, tháng, ngày của tháng)
            Calendar scheduleDateCalendar = Calendar.getInstance();
            scheduleDateCalendar.setTime(schedule.getBookingDate());

            boolean sameDay = scheduleDateCalendar.get(Calendar.YEAR) == dayCalendar.get(Calendar.YEAR) &&
                    scheduleDateCalendar.get(Calendar.MONTH) == dayCalendar.get(Calendar.MONTH) &&
                    scheduleDateCalendar.get(Calendar.DAY_OF_MONTH) == dayCalendar.get(Calendar.DAY_OF_MONTH);

            // So sánh khung giờ trực tiếp
            boolean sameTimeSlot = schedule.getTimeSlot().equals(timeSlotInRow);

            if (sameDay && sameTimeSlot) {
                return schedule;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return timeSlots != null ? timeSlots.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeSlot;
        // Đã thay đổi từ mảng sang một thể hiện duy nhất cho ngày được chọn
        FrameLayout flSelectedDayContent;
        LinearLayout llBookedContent;
        TextView tvStatus;
        TextView tvCustomerName;
        TextView tvCustomerPhone;
        TextView tvAvailable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);

            // Gán các view từ layout item_schedule.xml mới
            flSelectedDayContent = itemView.findViewById(R.id.fl_selected_day_content);
            llBookedContent = itemView.findViewById(R.id.ll_booked_content);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvCustomerPhone = itemView.findViewById(R.id.tv_customer_phone);
            tvAvailable = itemView.findViewById(R.id.tv_available);

            // Phương thức resetCells không còn cần thiết hoặc có thể được loại bỏ/đơn giản hóa
            // vì onBindViewHolder giờ sẽ xử lý trạng thái của một ô duy nhất
        }
    }
}