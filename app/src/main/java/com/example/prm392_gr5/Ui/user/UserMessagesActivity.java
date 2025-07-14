package com.example.prm392_gr5.Ui.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_gr5.Data.adapter.MessageAdapter;
import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.Data.repository.ChatMessageRepository;
import com.example.prm392_gr5.Data.repository.PitchOwnerProfileRepository;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.UserProfileRepository;
import com.example.prm392_gr5.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserMessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private ChatMessageRepository messageRepo;

    private String pitchName;
    private String phoneNumber;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_messages);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        ImageButton btnSend = findViewById(R.id.btnSend);
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        TextView tvPitchInfo = findViewById(R.id.tvPitchInfo);

        messageRepo = new ChatMessageRepository(this);

        pitchName = getIntent().getStringExtra("pitchName");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        userId = getIntent().getIntExtra("userId", -1);

        if (pitchName != null && phoneNumber != null) {
            tvPitchInfo.setText("Nhắn tin với: " + pitchName + " (" + phoneNumber + ")");
        } else {
            tvPitchInfo.setText("Không tìm thấy thông tin sân");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages = messageRepo.getMessagesByPitch(pitchName);
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty() && pitchName != null && userId != -1) {
                String currentTime = new SimpleDateFormat("HH:mm, dd/MM/yyyy").format(new Date());

                // ✅ Lấy tên user cố định
                String userName = new UserProfileRepository(this).getFullNameFromUserId(userId);

                Message newMessage = new Message(0, userName, messageText, currentTime, pitchName, userId);
                messageRepo.addMessage(newMessage);

                // ✅ Gửi auto-reply 1 lần duy nhất nếu chưa có
                if (!hasAutoReply(messages)) {
                    String pitchDisplayName = pitchName;
                    Message reply = new Message(0, pitchDisplayName, "Đã nhận, tôi sẽ liên hệ lại!", currentTime, pitchName, 0);
                    messageRepo.addMessage(reply);
                }

                messages.clear();
                messages.addAll(messageRepo.getMessagesByPitch(pitchName));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
                editTextMessage.setText("");
            }
        });
    }

    // ✅ Check auto-reply
    private boolean hasAutoReply(List<Message> messages) {
        for (Message msg : messages) {
            if (msg.getMessage().equals("Đã nhận, tôi sẽ liên hệ lại!")) {
                return true;
            }
        }
        return false;
    }
}
