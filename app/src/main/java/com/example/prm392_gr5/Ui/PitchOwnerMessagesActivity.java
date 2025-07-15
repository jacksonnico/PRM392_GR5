package com.example.prm392_gr5.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.adapter.MessageAdapter;
import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.Data.repository.ChatMessageRepository;
import com.example.prm392_gr5.Data.repository.UserProfileRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.auth.SharedPreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PitchOwnerMessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private ChatMessageRepository messageRepo;

    private String pitchName;
    private String phoneNumber;
    private String userName;
    private int userId;
    private int ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        ImageButton btnSend = findViewById(R.id.btnSend);
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        TextView tvPitchInfo = findViewById(R.id.tvPitchInfo);
        ImageView btnBack = findViewById(R.id.btnBack);

        messageRepo = new ChatMessageRepository(this);

        ownerId = SharedPreferencesHelper.getUserId(this);

        // Nhận dữ liệu từ Intent
        pitchName = getIntent().getStringExtra("pitchName");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        userId = getIntent().getIntExtra("userId", -1);
        userName = getIntent().getStringExtra("userName");

        if (pitchName == null || userId == -1 || userName == null) {
            Toast.makeText(this, "Không thể tải thông tin cuộc trò chuyện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvPitchInfo.setText("Nhắn tin với: " + userName + " (" + phoneNumber + ")");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages = messageRepo.getMessagesByPitch(pitchName);
        adapter = new MessageAdapter(messages, 0, ownerId);
        recyclerView.setAdapter(adapter);

        if (!messages.isEmpty()) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }

        btnSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                String currentTime = new SimpleDateFormat("HH:mm, dd/MM/yyyy").format(new Date());
                String ownerName = new UserProfileRepository(this).getFullNameFromUserId(ownerId);

                Message newMessage = new Message(0, ownerName, messageText, currentTime, pitchName, 0, ownerId);
                if (messageRepo.addMessage(newMessage, userId)) {
                    messages.clear();
                    messages.addAll(messageRepo.getMessagesByPitch(pitchName));
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size() - 1);
                    editTextMessage.setText("");
                } else {
                    Toast.makeText(this, "Gửi tin nhắn thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập nội dung tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
