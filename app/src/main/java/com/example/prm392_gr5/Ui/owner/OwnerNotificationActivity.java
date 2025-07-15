package com.example.prm392_gr5.Ui.owner;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.adapter.OwnerNotificationAdapter;
import com.example.prm392_gr5.Data.model.Notification;
import com.example.prm392_gr5.Data.repository.NotificationManagerRepository;
import com.example.prm392_gr5.R;

import java.util.List;

public class OwnerNotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OwnerNotificationAdapter adapter;
    private NotificationManagerRepository notificationRepo; // Thay DatabaseHelper bằng repository

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Setup Toolbar và xử lý nút back
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        recyclerView = findViewById(R.id.rvNotifications);
        notificationRepo = new NotificationManagerRepository(this);

        // Load danh sách thông báo của owner
        List<Notification> ownerNotifications = notificationRepo.getNotificationsForOwner();
        adapter = new OwnerNotificationAdapter(ownerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Đánh dấu tất cả đã đọc
        notificationRepo.markAllAsRead(0, "user"); // 👈 Owner đọc tất cả thông báo của user
    }
}
