package com.example.prm392_gr5.Ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_gr5.Data.model.Booking;
import com.example.prm392_gr5.Data.repository.BookingRepository;
import com.example.prm392_gr5.R;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView tvBookingTitle, tvBookingStatusDetail, tvPitchNameDetail,
            tvUserIdDetail, tvDateTimeDetail, tvServicesDetail, tvDepositAmountDetail;
    private Button btnChangeStatusDetail, btnBack;

    private BookingRepository bookingRepo;
    private Booking currentBooking;
    private int bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        // Get booking ID from intent
        bookingId = getIntent().getIntExtra("booking_id", -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy booking", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();

        bookingRepo = new BookingRepository(this);
        loadBookingDetail();
    }

    private void initViews() {
        tvBookingTitle = findViewById(R.id.tvBookingTitle);
        tvBookingStatusDetail = findViewById(R.id.tvBookingStatusDetail);
        tvPitchNameDetail = findViewById(R.id.tvPitchNameDetail);
        tvUserIdDetail = findViewById(R.id.tvUserIdDetail);
        tvDateTimeDetail = findViewById(R.id.tvDateTimeDetail);
        tvServicesDetail = findViewById(R.id.tvServicesDetail);
        tvDepositAmountDetail = findViewById(R.id.tvDepositAmountDetail);
        btnChangeStatusDetail = findViewById(R.id.btnChangeStatusDetail);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnChangeStatusDetail.setOnClickListener(v -> {
            if (currentBooking != null) {
                showStatusChangeDialog();
            }
        });
    }

    private void loadBookingDetail() {
        try {
            currentBooking = bookingRepo.getBookingById(bookingId);

            if (currentBooking != null) {
                displayBookingInfo();
                setTitle("Chi tiết booking #" + currentBooking.getId());
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin booking", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tải thông tin booking", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
    }

    private void displayBookingInfo() {
        tvBookingTitle.setText("Booking #" + currentBooking.getId());
        tvBookingStatusDetail.setText(currentBooking.getStatus());
        tvPitchNameDetail.setText(currentBooking.getPitchName());

        // Hiển thị tên người đặt
        String userDisplay = currentBooking.getUserName() != null
                ? currentBooking.getUserName()
                : "User #" + currentBooking.getUserId();
        tvUserIdDetail.setText("Người đặt: " + userDisplay);

        tvDateTimeDetail.setText(currentBooking.getDateTime());

        // Hiển thị tên dịch vụ
        if (currentBooking.getServiceNames() != null && !currentBooking.getServiceNames().isEmpty()) {
            tvServicesDetail.setText("Dịch vụ: " + String.join(", ", currentBooking.getServiceNames()));
        } else if (currentBooking.getServices() != null && !currentBooking.getServices().isEmpty()) {
            tvServicesDetail.setText("Dịch vụ (ID): " + currentBooking.getServices());
        } else {
            tvServicesDetail.setText("Không có dịch vụ");
        }

        tvDepositAmountDetail.setText(String.format("%,.0fđ", currentBooking.getDepositAmount()));

        // Set status color
        setStatusColor(tvBookingStatusDetail, currentBooking.getStatus());
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

    private void showStatusChangeDialog() {
        StatusChangeDialogFragment dialog = StatusChangeDialogFragment.newInstance(
                currentBooking.getId(), currentBooking.getStatus());
        dialog.setOnStatusChangedListener(this::onStatusChanged);
        dialog.show(getSupportFragmentManager(), "StatusChangeDialog");
    }

    private void onStatusChanged(int bookingId, String newStatus) {
        // Reload booking detail
        loadBookingDetail();
        Toast.makeText(this, "Đã cập nhật trạng thái thành: " + newStatus, Toast.LENGTH_SHORT).show();
    }
}