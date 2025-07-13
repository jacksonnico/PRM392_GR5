package com.example.prm392_gr5.Ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.repository.NotificationManagerRepository;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.Data.repository.UserProfileRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.booking.BookingActivity;
import com.example.prm392_gr5.Ui.user.UserMessagesActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PitchDetailActivity extends AppCompatActivity {
    private ImageView ivPitchImage;
    private TextView tvPitchName, tvPitchAddress, tvPhone, tvPrice, tvOpenClose, tvLocationLabel;
    private Button btnMap, btnBook, btnMessage;

    private Pitch p;
    private int userId;
    private PitchRepository pitchRepo;
    private UserProfileRepository userProfileRepo; // Thêm repository cho người dùng

    private String pitchName;
    private String currentTime;
    private String userName;
    private int ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_detail);

        pitchRepo = new PitchRepository(this); // Khởi tạo PitchRepository
        userProfileRepo = new UserProfileRepository(this); // Khởi tạo UserProfileRepository

        int pitchId = getIntent().getIntExtra("pitchId", -1);
        userId = getIntent().getIntExtra("userId", -1);

        if (userId < 0) {
            userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);
            if (userId < 0) {
                Toast.makeText(this, "Vui lòng đăng nhập để nhắn tin!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        p = pitchRepo.getPitchById(pitchId); // Sử dụng PitchRepository
        if (p == null) {
            finish();
            return;
        }

        // ⚠️ Phải gán các biến này sau khi đã có pitch p
        pitchName = p.getName();
        currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        userName = userProfileRepo.getFullNameFromUserId(userId); // Sử dụng UserProfileRepository
        ownerId = p.getOwnerId();

        // 1. Gán View
        ivPitchImage    = findViewById(R.id.ivPitchImage);
        tvPitchName     = findViewById(R.id.tvPitchName);
        tvPitchAddress  = findViewById(R.id.tvPitchAddress);
        tvPhone         = findViewById(R.id.tvPhone);
        tvPrice         = findViewById(R.id.tvPrice);
        tvOpenClose     = findViewById(R.id.tvOpenClose);
        tvLocationLabel = findViewById(R.id.tvLocationLabel);
        btnMap          = findViewById(R.id.btnMap);
        btnBook         = findViewById(R.id.btnBook);
        btnMessage      = findViewById(R.id.btnMessage);

        // 2. Hiển thị thông tin sân
        tvPitchName.setText(p.getName());
        tvPitchAddress.setText("Địa chỉ: " + p.getAddress());
        tvPhone.setText("SĐT: " + p.getPhoneNumber());
        tvPrice.setText("Giá: " + String.format("%,.0f", p.getPrice()) + " VND");
        tvOpenClose.setText("Giờ mở cửa: " + p.getOpenTime() + " - " + p.getCloseTime());

        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            if (p.getImageUrl().startsWith("http")) {
                Glide.with(this)
                        .load(p.getImageUrl())
                        .into(ivPitchImage);
            }
        }

        // 3. Bản đồ
        // 6. Click-to-call functionality for phone number
        tvPhone.setOnClickListener(v -> {
            String phoneNumber = p.getPhoneNumber();
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        // 7. "Xem bản đồ" khi bấm btnMap
        btnMap.setOnClickListener(v -> {
            String addr = p.getAddress();
            Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(addr));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Không tìm thấy ứng dụng bản đồ", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. Toolbar back
        // 8. Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 5. Đặt sân
        // 9. "Đặt sân" khi bấm btnBook
        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("pitchId", p.getId());
            startActivity(intent);

            // Gửi thông báo (cần NotificationManagerRepository)
            NotificationManagerRepository notificationRepo = new NotificationManagerRepository(this);
            notificationRepo.addNotification("Đặt sân thành công: " + pitchName, currentTime, userId, "user");
            notificationRepo.addNotification("Người dùng " + userName + " đã đặt sân: " + pitchName, currentTime, ownerId, "owner");
        });

        // 6. Nhắn tin
        btnMessage.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserMessagesActivity.class);
            intent.putExtra("pitchName", p.getName());
            intent.putExtra("phoneNumber", p.getPhoneNumber());
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }
}