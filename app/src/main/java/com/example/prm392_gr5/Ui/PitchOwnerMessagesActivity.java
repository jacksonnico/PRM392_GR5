package com.example.prm392_gr5.Ui;

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
import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PitchOwnerMessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private String pitchName;
    private int ownerId;
    private int userId;

    private TextView tvPitchInfo;
    private EditText editTextMessage;
    private ImageButton btnSend;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_owner_messages);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        btnSend = findViewById(R.id.btnSend);
        editTextMessage = findViewById(R.id.editTextMessage);
        tvPitchInfo = findViewById(R.id.tvPitchInfo);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);

        pitchName = getIntent().getStringExtra("pitchName");
        ownerId = getIntent().getIntExtra("ownerId", 0);
        userId = getIntent().getIntExtra("userId", 0);

        String fullName = getIntent().getStringExtra("fullName");
        String phone = getIntent().getStringExtra("phoneNumber");

        if (fullName != null && phone != null) {
            tvPitchInfo.setText("Nhắn với: " + fullName + " - " + phone);
        } else {
            tvPitchInfo.setText("Người dùng không xác định");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages = dbHelper.getMessagesByPitch(pitchName);
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            Log.d("PitchOwnerMessagesActivity", "Sending message - text: " + messageText + ", ownerId: " + ownerId + ", userId: " + userId);
            if (!messageText.isEmpty()) {
                String currentTime = new SimpleDateFormat("HH:mm, dd/MM/yyyy").format(new Date());
                Message newMessage = new Message(0, "Owner_" + ownerId, messageText, currentTime, pitchName, 0);
                try {
                    dbHelper.addMessage(newMessage);
                    // Lấy lại danh sách từ database để đảm bảo đồng bộ
                    messages.clear();
                    messages.addAll(dbHelper.getMessagesByPitch(pitchName));
                    adapter.notifyDataSetChanged(); // Dùng notifyDataSetChanged thay vì notifyItemInserted
                    recyclerView.scrollToPosition(messages.size() - 1);
                    editTextMessage.setText("");
                } catch (Exception e) {
                    Log.e("PitchOwnerMessagesActivity", "Error adding message: " + e.getMessage());
                }
            } else {
                Log.e("PitchOwnerMessagesActivity", "Message text is empty");
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}