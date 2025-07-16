package com.example.prm392_gr5.Ui.owner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.repository.OwnerDashboardRepository;
import com.example.prm392_gr5.R;

public class OwnerDashboardActivity extends AppCompatActivity {

    private OwnerDashboardRepository dashboardRepo;
    private DatabaseHelper dbHelper;
    private TextView tvTotalPitches, tvTotalBookings, tvPendingBookings, tvTotalRevenue;
    private CardView cardManagePitches,  cardPitchSchedule, cardAddServices, cardMessages;
    private int ownerId;
    private int userId; // ✅ Thêm biến userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        dashboardRepo = new OwnerDashboardRepository(this);
        dbHelper = new DatabaseHelper(this);

        // ✅ Lấy ownerId từ Intent hoặc dùng mặc định
        ownerId = getIntent().getIntExtra("ownerId", 2);
        userId = ownerId; // ✅ Vì đây là chủ sân → userId = ownerId

        initViews();
        loadDashboardData();
        setClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initViews() {
        tvTotalPitches = findViewById(R.id.tv_total_pitches);
        tvTotalBookings = findViewById(R.id.tv_total_bookings);
        tvPendingBookings = findViewById(R.id.tv_pending_bookings);

        cardManagePitches = findViewById(R.id.card_manage_pitches);
        cardPitchSchedule = findViewById(R.id.card_pitch_schedule);
        cardAddServices = findViewById(R.id.card_add_services);
        cardMessages = findViewById(R.id.card_messages);
    }

    private void loadDashboardData() {
        tvTotalPitches.setText(String.valueOf(dashboardRepo.getTotalPitches(ownerId)));
        tvTotalBookings.setText(String.valueOf(dashboardRepo.getTotalBookings(ownerId)));
        tvPendingBookings.setText(String.valueOf(dashboardRepo.getPendingBookings(ownerId)));

    }

    private void setClickListeners() {
        cardManagePitches.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagePitchActivity.class);
            intent.putExtra("ownerId", ownerId);
            startActivity(intent);
        });


        cardPitchSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, PitchScheduleActivity.class);
            intent.putExtra("ownerId", ownerId);
            startActivity(intent);
        });

        cardAddServices.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageServicesActivity.class);
            intent.putExtra("ownerId", ownerId);
            startActivity(intent);
        });

        // ✅ Gửi ownerId và userId khi mở UserMessageListActivity
        cardMessages.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserMessageListActivity.class);
            intent.putExtra("ownerId", ownerId);
            intent.putExtra("userId", userId); // ✅ Đã thêm
            startActivity(intent);
        });
    }
}
