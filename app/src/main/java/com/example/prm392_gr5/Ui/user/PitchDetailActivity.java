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
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.booking.BookingActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class PitchDetailActivity extends AppCompatActivity {
    private ImageView ivPitchImage;
    private TextView tvPitchName, tvPitchAddress, tvPhone, tvPrice, tvOpenClose, tvLocationLabel;
    private Button btnMap, btnBook;
    private Pitch p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_detail);

        // 1. Lấy pitchId từ Intent
        int pitchId = getIntent().getIntExtra("pitchId", -1);
        if (pitchId < 0) {
            finish();
            return;
        }

        // 2. Bind view
        ivPitchImage    = findViewById(R.id.ivPitchImage);
        tvPitchName     = findViewById(R.id.tvPitchName);
        tvPitchAddress  = findViewById(R.id.tvPitchAddress);
        tvPhone         = findViewById(R.id.tvPhone);
        tvPrice         = findViewById(R.id.tvPrice);
        tvOpenClose     = findViewById(R.id.tvOpenClose);
        tvLocationLabel = findViewById(R.id.tvLocationLabel);
        btnMap          = findViewById(R.id.btnMap);
        btnBook         = findViewById(R.id.btnBook);

        // 3. Load dữ liệu từ DB
        p = new PitchRepository(this).getPitchById(pitchId);
        if (p == null) {
            finish();
            return;
        }

        // 4. Hiển thị thông tin text
        tvPitchName.setText(p.getName());
        tvPitchAddress.setText("Địa chỉ: " + p.getAddress());
        tvPhone.setText("SĐT: " + p.getPhoneNumber());
        tvPrice.setText("Giá: " + String.format("%, .0f", p.getPrice()) + " VND");
        tvOpenClose.setText("Giờ mở cửa: " + p.getOpenTime() + " - " + p.getCloseTime());

        // 5. Load ảnh với Glide
        if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
            if (p.getImageUrl().startsWith("http")) {
                Glide.with(this)
                        .load(p.getImageUrl())
                        .into(ivPitchImage);
            }
        }

        // 6. Click-to-call functionality for phone number
        tvPhone.setOnClickListener(v -> {
            String phoneNumber = p.getPhoneNumber();
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                try {
                    // Create intent to make phone call
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

        // 8. Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 9. "Đặt sân" khi bấm btnBook
        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("pitchId", p.getId());
            startActivity(intent);
        });
    }
}