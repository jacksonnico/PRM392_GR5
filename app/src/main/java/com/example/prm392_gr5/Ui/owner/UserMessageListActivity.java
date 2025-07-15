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
    private OwnerChatSummaryRepository summaryRepo;
    private int ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message_list);

        recyclerView = findViewById(R.id.recyclerViewUserMessages);
        summaryRepo = new OwnerChatSummaryRepository(this);

        // Nhận ownerId từ Intent hoặc mặc định
        ownerId = getIntent().getIntExtra("ownerId", 0);
        if (ownerId == 0) {
            ownerId = 2; // ✅ Giá trị mặc định
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<UserMessageSummary> messageSummaries = summaryRepo.getUserMessageSummariesForOwner(ownerId);

        // ✅ Gọi đúng constructor: (List, ownerId, context)
        adapter = new UserMessageAdapter(messageSummaries, ownerId, this);
        recyclerView.setAdapter(adapter);
    }
}
