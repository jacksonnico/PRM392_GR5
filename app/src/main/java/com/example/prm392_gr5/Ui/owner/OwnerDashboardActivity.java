package com.example.prm392_gr5.Ui.owner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.example.prm392_gr5.Data.repository.OwnerDashboardRepository;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.R;

public class OwnerDashboardActivity extends AppCompatActivity {
    private OwnerDashboardRepository dashboardRepo;
    private DatabaseHelper dbHelper;
    private TextView tvTotalPitches, tvTotalBookings, tvPendingBookings, tvTotalRevenue;
    private CardView cardManagePitches, cardApproveBookings, cardPitchSchedule, cardAddServices, cardStats;
    private int ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        dashboardRepo = new OwnerDashboardRepository(this);
        ownerId = 2;

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
        cardApproveBookings = findViewById(R.id.card_approve_bookings);
        cardPitchSchedule = findViewById(R.id.card_pitch_schedule);
        cardAddServices = findViewById(R.id.card_add_services);

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

        cardApproveBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, ApproveBookingActivity.class);
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

    }
}