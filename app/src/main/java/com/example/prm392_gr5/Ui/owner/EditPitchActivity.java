package com.example.prm392_gr5.Ui.owner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;

public class EditPitchActivity extends AppCompatActivity {

    private EditText etName, etPrice, etAddress, etPhone, etOpenTime, etCloseTime, etImageUrl;
    private Button btnUpdate, btnCancel;

    private PitchRepository pitchRepository;
    private int pitchId;
    private Pitch pitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pitch);

        pitchId = getIntent().getIntExtra("pitchId", -1);
        if (pitchId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID sân bóng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pitchRepository = new PitchRepository(this);
        initViews();
        loadData();
        setListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_pitch_name);
        etPrice = findViewById(R.id.et_pitch_price);
        etAddress = findViewById(R.id.et_pitch_address);
        etPhone = findViewById(R.id.et_pitch_phone);
        etOpenTime = findViewById(R.id.et_open_time);
        etCloseTime = findViewById(R.id.et_close_time);
        etImageUrl = findViewById(R.id.et_image_url);
        btnUpdate = findViewById(R.id.btn_update_pitch);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void loadData() {
        pitch = pitchRepository.getPitchById(pitchId);
        if (pitch == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu sân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName.setText(pitch.getName());
        etPrice.setText(String.valueOf(pitch.getPrice()));
        etAddress.setText(pitch.getAddress());
        etPhone.setText(pitch.getPhoneNumber());
        etOpenTime.setText(pitch.getOpenTime());
        etCloseTime.setText(pitch.getCloseTime());
        etImageUrl.setText(pitch.getImageUrl());
    }

    private void setListeners() {
        btnUpdate.setOnClickListener(v -> updatePitch());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updatePitch() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Tên và Giá", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            pitch.setName(name);
            pitch.setPrice(Double.parseDouble(priceStr));
            pitch.setAddress(etAddress.getText().toString().trim());
            pitch.setPhoneNumber(etPhone.getText().toString().trim());
            pitch.setOpenTime(etOpenTime.getText().toString().trim());
            pitch.setCloseTime(etCloseTime.getText().toString().trim());
            pitch.setImageUrl(etImageUrl.getText().toString().trim());

            int rowsAffected = pitchRepository.updatePitch(pitch);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }



        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}
