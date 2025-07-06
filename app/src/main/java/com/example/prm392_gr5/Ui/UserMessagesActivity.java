package com.example.prm392_gr5.Ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.EditText;
import com.example.prm392_gr5.Data.adapter.MessageAdapter;
import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.Data.model.Message;
import com.example.prm392_gr5.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UserMessagesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_messages); // Sử dụng chung layout

        recyclerView = findViewById(R.id.recyclerViewMessages);
        ImageButton btnSend = findViewById(R.id.btnSend);
        EditText editTextMessage = findViewById(R.id.editTextMessage);

        dbHelper = new DatabaseHelper(this);

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages = dbHelper.getMessages("user"); // User xem tin nhắn của mình
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);

        // Xử lý nút gửi
        btnSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
                String currentTime = sdf.format(new java.util.Date());
                Message newMessage = new Message(0, "User", messageText, currentTime, "admin"); // Gửi đến Admin
                dbHelper.addMessage(newMessage);
                messages.add(newMessage);
                adapter.notifyDataSetChanged();
                editTextMessage.setText("");
            }
        });
    }
}