package com.example.prm392_gr5.Ui.user;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.adapter.NotificationAdapter;
import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Notification;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.auth.SharedPreferencesHelper;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    NotificationAdapter adapter;
    DatabaseHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.rvNotifications);
        db = new DatabaseHelper(this);
        userId = SharedPreferencesHelper.getUserId(this);

        // Sửa lại chỗ này
        List<Notification> list = db.getNotifications(userId, "user");
        adapter = new NotificationAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
