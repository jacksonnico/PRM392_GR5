package com.example.prm392_gr5.Ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Data.repository.*;
import com.example.prm392_gr5.Ui.auth.LoginActivity;

public class AdminMainActivity extends AppCompatActivity {
    private UserRepository userRepo;
    private PitchRepository pitchRepo;
    private BookingRepository bookingRepo;
    private PaymentRepository paymentRepo;

    // Quick stats views
    private TextView tvTotalUsers, tvTotalOwners, tvTotalPitches, tvTotalBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        initRepositories();
        initViews();
        loadQuickStats();
        setupClickListeners();
    }

    private void initRepositories() {
        userRepo = new UserRepository(this);
        pitchRepo = new PitchRepository(this);
        bookingRepo = new BookingRepository(this);
        paymentRepo = new PaymentRepository(this);
    }

    private void initViews() {
        setTitle("Admin Dashboard");

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalOwners = findViewById(R.id.tvTotalOwners);
        tvTotalPitches = findViewById(R.id.tvTotalPitches);
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
    }

    private void loadQuickStats() {
        try {
            int totalUsers = userRepo.getUserCountByRole("user");
            int totalOwners = userRepo.getUserCountByRole("owner");
            int totalPitches = pitchRepo.getTotalPitchCount();
            int totalBookings = bookingRepo.getBookingCount();

            tvTotalUsers.setText(String.valueOf(totalUsers));
            tvTotalOwners.setText(String.valueOf(totalOwners));
            tvTotalPitches.setText(String.valueOf(totalPitches));
            tvTotalBookings.setText(String.valueOf(totalBookings));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        // Quản lý người dùng
        CardView cardManageUsers = findViewById(R.id.cardManageUsers);
        cardManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageUsersActivity.class));
        });

        // Quản lý chủ sân
        CardView cardManageOwners = findViewById(R.id.cardManageOwners);
        cardManageOwners.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageOwnersActivity.class));
        });

        // Quản lý sân
        CardView cardManagePitches = findViewById(R.id.cardManagePitches);
        cardManagePitches.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageOwnerPitchesActivity.class));
        });

        // Dashboard & Thống kê
        CardView cardDashboard = findViewById(R.id.cardDashboard);
        cardDashboard.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminDashboardActivity.class));
        });

        // Thống kê chi tiết
        CardView cardStatistics = findViewById(R.id.cardStatistics);
        cardStatistics.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminStatisticsActivity.class));
        });

        // Quản lý booking (nếu có)
        CardView cardManageBookings = findViewById(R.id.cardManageBookings);
        cardManageBookings.setOnClickListener(v -> {
            // TODO: Tạo ManageBookingsActivity
            startActivity(new Intent(this, ManageBookingsActivity.class));
        });

        CardView cardLogout = findViewById(R.id.cardLogout);
        cardLogout.setOnClickListener(v -> {
            // Xử lý đăng xuất
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        // Xoá session, ví dụ sharedPreferences
                        getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().clear().apply();

                        // Quay về màn hình đăng nhập
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
    });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuickStats(); // Refresh stats khi quay lại
    }
}