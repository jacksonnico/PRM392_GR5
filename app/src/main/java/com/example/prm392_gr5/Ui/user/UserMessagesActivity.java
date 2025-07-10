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
import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserMessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private String pitchName;
    private String phoneNumber;
    private int userId; // ✅ userId thực sự

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_messages);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        ImageButton btnSend = findViewById(R.id.btnSend);
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        TextView tvPitchInfo = findViewById(R.id.tvPitchInfo);

        dbHelper = new DatabaseHelper(this);

        // ✅ Lấy thông tin từ intent
        pitchName = getIntent().getStringExtra("pitchName");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        userId = getIntent().getIntExtra("userId", -1); // default -1 nếu chưa có
        Log.d("UserMessagesActivity", "userId nhận vào: " + userId);

        if (pitchName != null && phoneNumber != null) {
            tvPitchInfo.setText("Nhắn tin với: " + pitchName + " (" + phoneNumber + ")");
        } else {
            tvPitchInfo.setText("Không tìm thấy thông tin sân");
        }

        // ✅ Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages = dbHelper.getMessagesByPitch(pitchName);
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            Log.d("UserMessagesActivity", "Sending message - text: " + messageText + ", pitchName: " + pitchName + ", userId: " + userId);
            if (!messageText.isEmpty() && pitchName != null && userId != -1) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
                String currentTime = sdf.format(new Date());
                Message newMessage = new Message(0, "", messageText, currentTime, pitchName, userId);
                dbHelper.addMessage(newMessage);
                Message reply = new Message(0, "", "Đã nhận, tôi sẽ liên hệ lại!", currentTime, pitchName, 0);
                dbHelper.addMessage(reply);
                messages.clear();
                messages.addAll(dbHelper.getMessagesByPitch(pitchName)); // Đồng bộ từ database
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
                editTextMessage.setText("");
            } else {
                Log.e("UserMessagesActivity", "Cannot send: messageText=" + messageText + ", pitchName=" + pitchName + ", userId=" + userId);
            }
        });
    }
}
