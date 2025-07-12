package com.example.prm392_gr5.Ui.owner;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.adapter.UserMessageAdapter;
import com.example.prm392_gr5.Data.model.UserMessageSummary;
import com.example.prm392_gr5.Data.repository.OwnerChatSummaryRepository;
import com.example.prm392_gr5.R;

import java.util.List;

public class UserMessageListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserMessageAdapter adapter;
    private OwnerChatSummaryRepository summaryRepo; // Thay DatabaseHelper bằng repository
    private int ownerId;
    private int userId; // ✅ Thêm biến userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message_list);

        recyclerView = findViewById(R.id.recyclerViewUserMessages);
        summaryRepo = new OwnerChatSummaryRepository(this); // Khởi tạo repository với Context

        // Nhận ownerId và userId từ Intent
        ownerId = getIntent().getIntExtra("ownerId", 0);
        userId = getIntent().getIntExtra("userId", 0); // ✅ Lấy userId

        if (ownerId == 0) {
            ownerId = 2; // Giá trị mặc định
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<UserMessageSummary> messageSummaries = summaryRepo.getUserMessageSummariesForOwner(ownerId); // Sử dụng repository

        // ✅ Gọi đúng constructor với 3 tham số
        adapter = new UserMessageAdapter(messageSummaries, ownerId, userId);
        recyclerView.setAdapter(adapter);
    }
}