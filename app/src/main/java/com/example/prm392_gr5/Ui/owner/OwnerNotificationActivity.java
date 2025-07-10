package com.example.prm392_gr5.Ui.owner;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.adapter.OwnerNotificationAdapter;
import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Notification;
import com.example.prm392_gr5.R;

import java.util.List;

public class OwnerNotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OwnerNotificationAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification); // Dùng chung layout với user

        recyclerView = findViewById(R.id.rvNotifications);
        db = new DatabaseHelper(this);

        // Lấy danh sách thông báo của owner
        List<Notification> ownerNotifications = db.getNotificationsForOwner();

        // Adapter riêng cho owner
        adapter = new OwnerNotificationAdapter(ownerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
