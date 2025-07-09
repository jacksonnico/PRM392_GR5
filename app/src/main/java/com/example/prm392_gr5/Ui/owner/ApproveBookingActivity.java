package com.example.prm392_gr5.Ui.owner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Booking;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.Data.model.BookingInfo;
import com.example.prm392_gr5.Ui.owner.adapter.BookingAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApproveBookingActivity extends AppCompatActivity implements BookingAdapter.OnBookingActionListener, BookingAdapter.OnBookingStatusChangedListener {
    private PitchScheduleActivity pitchSchedule;
    private BookingRepository bookingRepository;
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<BookingInfo> bookingList;
    private int ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_booking);
        bookingRepository = new BookingRepository(this);
        ownerId = 2;
        initViews();
        loadPendingBookings();
    }
    @Override
    public void onStatusChanged() {

        pitchSchedule.loadSchedule();
    }
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_bookings);

        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadPendingBookings() {
        bookingList.clear();

        // Lấy danh sách Booking từ Repository
        List<Booking> bookings = bookingRepository.getPendingBookings(ownerId);

        // Chuyển sang BookingInfo
        for (Booking b : bookings) {
            bookingList.add(new BookingInfo(b));
        }

        adapter.updateBookings(bookingList);
    }

    private void updateBookingStatus(int bookingId, String status) {
        boolean success = bookingRepository.updateBookingStatus(bookingId, status);
        if (success) {
            Toast.makeText(this, status.equals("approved") ? "Đã duyệt booking" : "Đã từ chối booking", Toast.LENGTH_SHORT).show();
            loadPendingBookings();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBookingDetails(BookingInfo booking) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_booking_details, null);

        TextView tvUserName = dialogView.findViewById(R.id.tv_user_name);
        TextView tvUserPhone = dialogView.findViewById(R.id.tv_user_phone);
        TextView tvPitchName = dialogView.findViewById(R.id.tv_pitch_name);
        TextView tvDateTime = dialogView.findViewById(R.id.tv_date_time);
        TextView tvServices = dialogView.findViewById(R.id.tv_services);
        TextView tvDeposit = dialogView.findViewById(R.id.tv_deposit);

        tvUserName.setText(booking.userName);
        tvUserPhone.setText(booking.userPhone);
        tvPitchName.setText(booking.pitchName);
        tvDateTime.setText(formatDateTime(booking.dateTime));
        tvServices.setText(getServicesText(booking.services, booking.pitchId));
        tvDeposit.setText(String.format("%.0f VNĐ", booking.depositAmount));

        new AlertDialog.Builder(this)
                .setTitle("Chi tiết booking")
                .setView(dialogView)
                .setPositiveButton("Duyệt", (dialog, which) -> {
                    updateBookingStatus(booking.id, "approved");
                })
                .setNegativeButton("Từ chối", (dialog, which) -> {
                    updateBookingStatus(booking.id, "rejected");
                })
                .setNeutralButton("Đóng", null)
                .show();
    }

    private String formatDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "Chưa có thời gian";
        }

        try {

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

    private String getServicesText(String servicesJson, int pitchId) {
        return bookingRepository.getServiceText(servicesJson);
    }

    // Implement interface methods
    @Override
    public void onApproveClick(int bookingId) {
        updateBookingStatus(bookingId, "approved");
        onStatusChanged();
    }

    @Override
    public void onRejectClick(int bookingId) {
        updateBookingStatus(bookingId, "rejected");
        onStatusChanged();
    }

    @Override
    public void onBookingClick(BookingInfo booking) {
        showBookingDetails(booking);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPendingBookings();
    }
}