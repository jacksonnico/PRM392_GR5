package com.example.prm392_gr5.Ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.adapter.MessageAdapter;
import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.Data.repository.ChatMessageRepository;
import com.example.prm392_gr5.Data.repository.PitchOwnerProfileRepository;
import com.example.prm392_gr5.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PitchOwnerMessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private ChatMessageRepository messageRepo;
    private PitchOwnerProfileRepository ownerRepo;

    private String pitchName;
    private int ownerId;
    private int userId;
    private String ownerName;
    private String customerName;

    private TextView tvPitchInfo;
    private EditText editTextMessage;
    private ImageButton btnSend;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_owner_messages);

        initViews();
        initRepositories();
        getIntentData();
        setupUI();
        loadMessages();
        setupClickListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewMessages);
        btnSend = findViewById(R.id.btnSend);
        editTextMessage = findViewById(R.id.editTextMessage);
        tvPitchInfo = findViewById(R.id.tvPitchInfo);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initRepositories() {
        messageRepo = new ChatMessageRepository(this);
        ownerRepo = new PitchOwnerProfileRepository(this);
    }

    private void getIntentData() {
        pitchName = getIntent().getStringExtra("pitchName");
        ownerId = getIntent().getIntExtra("ownerId", 0);
        userId = getIntent().getIntExtra("userId", 0);

        // ✅ Lấy tên chủ sân
        ownerName = getIntent().getStringExtra("fullName");
        if (ownerName == null || ownerName.isEmpty()) {
            ownerName = ownerRepo.getFullNameFromOwnerId(ownerId);
        }
        if (ownerName == null || ownerName.isEmpty()) {
            ownerName = "Chủ sân";
        }

        customerName = getIntent().getStringExtra("customerName");
        if (customerName == null || customerName.isEmpty()) {
            customerName = "Khách hàng";
        }

        Log.d("PitchOwnerMessages", "Pitch: " + pitchName + ", Owner: " + ownerName +
                ", Customer: " + customerName + ", OwnerId: " + ownerId + ", UserId: " + userId);
    }

    private void setupUI() {
        // ✅ Hiển thị thông tin cuộc trò chuyện
        if (pitchName != null) {
            tvPitchInfo.setText(pitchName + " - " + customerName);
        } else {
            tvPitchInfo.setText("Cuộc trò chuyện");
        }

        // ✅ Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages, getCurrentUserId(), ownerId);
        recyclerView.setAdapter(adapter);
    }

    private void loadMessages() {
        if (pitchName != null) {
            messages.clear();
            messages.addAll(messageRepo.getMessagesByPitch(pitchName));
            adapter.updateMessages(messages);

            // ✅ Scroll to bottom
            if (messages.size() > 0) {
                recyclerView.scrollToPosition(messages.size() - 1);
            }

            Log.d("PitchOwnerMessages", "Loaded " + messages.size() + " messages");
        }
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void sendMessage(String messageText) {
        if (pitchName == null) {
            Log.e("PitchOwnerMessages", "Cannot send message: pitchName is null");
            return;
        }

        String currentTime = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault()).format(new Date());

        // ✅ Tạo message với tên chủ sân
        Message newMessage = new Message(0, ownerName, messageText, currentTime, pitchName, ownerId);

        try {
            messageRepo.addMessage(newMessage);
            Log.d("PitchOwnerMessages", "Message sent: " + ownerName + " - " + messageText);

            // ✅ Refresh messages
            loadMessages();
            editTextMessage.setText("");

        } catch (Exception e) {
            Log.e("PitchOwnerMessages", "Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getCurrentUserId() {
        // ✅ Lấy userId từ SharedPreferences hoặc session
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        return sharedPref.getInt("userId", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
    }
}