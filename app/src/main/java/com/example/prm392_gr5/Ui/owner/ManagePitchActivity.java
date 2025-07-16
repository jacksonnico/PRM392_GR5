package com.example.prm392_gr5.Ui.owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.repository.NotificationManagerRepository;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.auth.AccountActivity;
import com.example.prm392_gr5.Ui.owner.AddPitchActivity;
import com.example.prm392_gr5.Ui.owner.OwnerDashboardActivity;
import com.example.prm392_gr5.Ui.owner.OwnerNotificationActivity;
import com.example.prm392_gr5.Ui.owner.PitchScheduleActivity;
import com.example.prm392_gr5.Ui.user.NotificationActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ManagePitchActivity extends AppCompatActivity {

    private PitchRepository pitchRepository;
    private NotificationManagerRepository notificationRepository;
    private RecyclerView recyclerView;
    private com.example.prm392_gr5.Ui.owner.adapter.PitchAdapter adapter;
    private List<Pitch> pitchList;
    private Button fabAddPitch;
    private int ownerId;
    private BottomNavigationView bottomNavigationView;
    private TextView tvNoPitchesMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_pitch);

        pitchRepository = new PitchRepository(this);
        notificationRepository = new NotificationManagerRepository(this);
        ownerId = 2;  // Lấy ownerId từ SharedPreferences nếu cần

        initViews();
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_pitches);
        fabAddPitch = findViewById(R.id.fab_add_pitch);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        tvNoPitchesMessage = findViewById(R.id.tv_no_pitches_message);

        pitchList = new ArrayList<>();
        adapter = new com.example.prm392_gr5.Ui.owner.adapter.PitchAdapter(this, pitchList, ownerId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAddPitch.setOnClickListener(v -> {
            Intent intent = new Intent(ManagePitchActivity.this, AddPitchActivity.class);
            intent.putExtra("ownerId", ownerId);
            startActivity(intent);
        });
    }

    private void loadPitches() {
        pitchList.clear();
        pitchList.addAll(pitchRepository.getPitchesByOwner(ownerId));
        adapter.notifyDataSetChanged();

        if (pitchList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoPitchesMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoPitchesMessage.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_pitches);

        // Cập nhật badge số thông báo
        updateNotificationBadge();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;

                int itemId = item.getItemId();

                if (itemId == R.id.nav_pitches) {
                    return true;
                } else if (itemId == R.id.nav_dashboard) {
                    intent = new Intent(ManagePitchActivity.this, OwnerDashboardActivity.class);
                } else if (itemId == R.id.nav_schedule) {
                    intent = new Intent(ManagePitchActivity.this, PitchScheduleActivity.class);
                } else if (itemId == R.id.nav_notifications) {
                    intent = new Intent(ManagePitchActivity.this, OwnerNotificationActivity.class);
                    // Khi mở notifications => đánh dấu đã đọc
                    notificationRepository.markAllAsRead(ownerId, "owner");
                    clearNotificationBadge();
                } else if (itemId == R.id.nav_account) {
                    intent = new Intent(ManagePitchActivity.this, AccountActivity.class);
                }

                if (intent != null) {
                    intent.putExtra("ownerId", ownerId);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });
    }

    private void updateNotificationBadge() {
        int unreadCount = notificationRepository.getUnreadCount(ownerId, "owner");
        if (unreadCount > 0) {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_notifications);
            badge.setVisible(true);
            badge.setNumber(unreadCount);
        } else {
            clearNotificationBadge();
        }
    }

    private void clearNotificationBadge() {
        bottomNavigationView.removeBadge(R.id.nav_notifications);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPitches();
        updateNotificationBadge();
        bottomNavigationView.setSelectedItemId(R.id.nav_pitches);
    }
}
