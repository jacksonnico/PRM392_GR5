package com.example.prm392_gr5.Ui.user;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.adapter.NotificationAdapter;
import com.example.prm392_gr5.Data.model.Notification;
import com.example.prm392_gr5.Data.repository.NotificationManagerRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.auth.SharedPreferencesHelper;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    NotificationAdapter adapter;
    NotificationManagerRepository notificationRepo; // Thay DatabaseHelper bằng repository
    int userId;

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
        userId = SharedPreferencesHelper.getUserId(this);

        // Load danh sách thông báo
        List<Notification> list = notificationRepo.getNotifications(userId, "user");
        adapter = new NotificationAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Đánh dấu tất cả đã đọc
        notificationRepo.markAllAsRead(userId, "user");
    }
}
