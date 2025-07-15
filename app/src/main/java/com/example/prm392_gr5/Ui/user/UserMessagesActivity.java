package com.example.prm392_gr5.Ui.user;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.adapter.MessageAdapter;
import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.Data.repository.ChatMessageRepository;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.UserProfileRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.auth.SharedPreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserMessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private ChatMessageRepository messageRepo;
    private PitchRepository pitchRepo;

    private String pitchName, phoneNumber;
    private int userId, ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        ImageButton btnSend = findViewById(R.id.btnSend);
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        TextView tvPitchInfo = findViewById(R.id.tvPitchInfo);

        messageRepo = new ChatMessageRepository(this);
        pitchRepo = new PitchRepository(this);
        userId = SharedPreferencesHelper.getUserId(this);

        if (userId == -1) {
            Toast.makeText(this, "Bạn cần đăng nhập để nhắn tin", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pitchName = getIntent().getStringExtra("pitchName");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        ownerId = pitchRepo.getOwnerIdFromPitchName(pitchName);

        if (pitchName == null || ownerId == -1) {
            Toast.makeText(this, "Không thể tải thông tin cuộc trò chuyện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvPitchInfo.setText("Nhắn tin với: " + pitchName + " (" + phoneNumber + ")");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages = messageRepo.getMessagesByPitch(pitchName);
        adapter = new MessageAdapter(messages, userId, ownerId);
        recyclerView.setAdapter(adapter);

        if (!messages.isEmpty()) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }

        btnSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                String currentTime = new SimpleDateFormat("HH:mm, dd/MM/yyyy").format(new Date());
                String userName = new UserProfileRepository(this).getFullNameFromUserId(userId);

                Message newMessage = new Message(0, userName, messageText, currentTime, pitchName, userId, 0);
                if (messageRepo.addMessage(newMessage, ownerId)) {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        messages.clear();
        messages.addAll(messageRepo.getMessagesByPitch(pitchName));
        adapter.notifyDataSetChanged();
        if (!messages.isEmpty()) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }
}
