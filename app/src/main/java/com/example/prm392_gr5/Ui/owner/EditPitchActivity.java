package com.example.prm392_gr5.Ui.owner;

import android.app.TimePickerDialog; // Thêm import này nếu chưa có
import android.os.Bundle;
import android.text.InputType; // Thêm import này nếu chưa có
import android.util.Patterns; // Thêm import này nếu chưa có
import android.view.View; // Thêm import này nếu chưa có
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_gr5.Data.model.Pitch;
import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;

import java.util.Calendar; // Thêm import này nếu chưa có
import java.util.Locale; // Thêm import này nếu chưa có

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

        // Đặt lắng nghe click cho giờ mở/đóng cửa để mở TimePicker
        etOpenTime.setOnClickListener(this::showTimePickerDialog);
        etCloseTime.setOnClickListener(this::showTimePickerDialog);

        // Đảm bảo không cho nhập trực tiếp vào trường giờ, chỉ chọn qua TimePicker
        etOpenTime.setInputType(InputType.TYPE_NULL);
        etCloseTime.setInputType(InputType.TYPE_NULL);
        etOpenTime.setFocusable(false);
        etCloseTime.setFocusable(false);
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

    // Phương thức hiển thị TimePickerDialog
    private void showTimePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    if (v.getId() == R.id.et_open_time) {
                        etOpenTime.setText(formattedTime);
                    } else if (v.getId() == R.id.et_close_time) {
                        etCloseTime.setText(formattedTime);
                    }
                },
                hour, minute, true); // true cho định dạng 24 giờ
        timePickerDialog.show();
    }

    private void updatePitch() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String openTime = etOpenTime.getText().toString().trim();
        String closeTime = etCloseTime.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        Double price = null;
        try {
            if (!priceStr.isEmpty()) {
                price = Double.parseDouble(priceStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ. Vui lòng nhập số.", Toast.LENGTH_SHORT).show();
            etPrice.requestFocus();
            return;
        }


        if (!validateInput(name, address, phone, openTime, closeTime, price, imageUrl)) {
            return; // Nếu validate thất bại, dừng quá trình cập nhật
        }

        // Nếu validate thành công, tiến hành cập nhật
        pitch.setName(name);
        pitch.setPrice(price); // Đã kiểm tra null và định dạng ở validateInput
        pitch.setAddress(address);
        pitch.setPhoneNumber(phone);
        pitch.setOpenTime(openTime);
        pitch.setCloseTime(closeTime);
        pitch.setImageUrl(imageUrl);

        int rowsAffected = pitchRepository.updatePitch(pitch);
        if (rowsAffected > 0) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            // Thiết lập RESULT_OK và finish để Activity gọi (ví dụ PitchManagementActivity) có thể biết và refresh
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại. Không có thay đổi hoặc lỗi DB.", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateInput(String name, String address, String phone,
                                  String openTime, String closeTime, Double price, String imageUrl) {

        // Validate Tên sân
        if (name.isEmpty()) {
            Toast.makeText(this, "Tên sân không được để trống.", Toast.LENGTH_SHORT).show();
            etName.requestFocus();
            return false;
        }
        if (name.length() < 3) {
            Toast.makeText(this, "Tên sân phải có ít nhất 3 ký tự.", Toast.LENGTH_SHORT).show();
            etName.requestFocus();
            return false;
        }

        // Validate Giá
        if (price == null) { // Đã xử lý NumberFormatException bên trên
            Toast.makeText(this, "Giá/giờ không được để trống.", Toast.LENGTH_SHORT).show();
            etPrice.requestFocus();
            return false;
        }
        if (price <= 0) {
            Toast.makeText(this, "Giá/giờ phải lớn hơn 0.", Toast.LENGTH_SHORT).show();
            etPrice.requestFocus();
            return false;
        }

        // Validate Địa chỉ
        if (address.isEmpty()) {
            Toast.makeText(this, "Địa chỉ không được để trống.", Toast.LENGTH_SHORT).show();
            etAddress.requestFocus();
            return false;
        }
        if (address.length() < 5) { // Ví dụ: địa chỉ tối thiểu 5 ký tự
            Toast.makeText(this, "Địa chỉ phải có ít nhất 5 ký tự.", Toast.LENGTH_SHORT).show();
            etAddress.requestFocus();
            return false;
        }

        // Validate Số điện thoại
        if (phone.isEmpty()) {
            Toast.makeText(this, "Số điện thoại không được để trống.", Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return false;
        }
        // Sử dụng regex đơn giản cho số điện thoại (ví dụ: 7-15 chữ số)
        if (!phone.matches("^[0-9]{7,15}$")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ. Vui lòng nhập từ 7 đến 15 chữ số.", Toast.LENGTH_LONG).show();
            etPhone.requestFocus();
            return false;
        }

        // Validate Giờ mở cửa
        if (openTime.isEmpty()) {
            Toast.makeText(this, "Giờ mở cửa không được để trống.", Toast.LENGTH_SHORT).show();
            etOpenTime.requestFocus();
            return false;
        }
        // Validate định dạng giờ (HH:mm)
        if (!openTime.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            Toast.makeText(this, "Giờ mở cửa không hợp lệ. Định dạng HH:mm.", Toast.LENGTH_SHORT).show();
            etOpenTime.requestFocus();
            return false;
        }


        // Validate Giờ đóng cửa
        if (closeTime.isEmpty()) {
            Toast.makeText(this, "Giờ đóng cửa không được để trống.", Toast.LENGTH_SHORT).show();
            etCloseTime.requestFocus();
            return false;
        }
        // Validate định dạng giờ (HH:mm)
        if (!closeTime.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            Toast.makeText(this, "Giờ đóng cửa không hợp lệ. Định dạng HH:mm.", Toast.LENGTH_SHORT).show();
            etCloseTime.requestFocus();
            return false;
        }

        // Validate logic giờ mở/đóng cửa
        if (!validateBusinessHours(openTime, closeTime)) {
            // Toast đã được hiển thị bên trong validateBusinessHours
            return false;
        }

        // Validate URL ảnh
        if (imageUrl.isEmpty()) {
            Toast.makeText(this, "URL ảnh sân không được để trống.", Toast.LENGTH_SHORT).show();
            etImageUrl.requestFocus();
            return false;
        }
        if (!Patterns.WEB_URL.matcher(imageUrl).matches()) {
            Toast.makeText(this, "URL ảnh không hợp lệ. Vui lòng nhập đúng định dạng URL.", Toast.LENGTH_LONG).show();
            etImageUrl.requestFocus();
            return false;
        }

        return true; // Tất cả các trường đều hợp lệ
    }

    // Phương thức kiểm tra giờ đóng cửa phải sau giờ mở cửa
    private boolean validateBusinessHours(String openTime, String closeTime) {
        try {
            String[] openParts = openTime.split(":");
            String[] closeParts = closeTime.split(":");

            int openHour = Integer.parseInt(openParts[0]);
            int openMinute = Integer.parseInt(openParts[1]);
            int closeHour = Integer.parseInt(closeParts[0]);
            int closeMinute = Integer.parseInt(closeParts[1]);

            int openMinutes = openHour * 60 + openMinute;
            int closeMinutes = closeHour * 60 + closeMinute;

            // Giờ đóng cửa phải sau giờ mở cửa.
            // Nếu là cùng một ngày, closeMinutes phải > openMinutes.
            // Nếu qua ngày, closeMinutes có thể nhỏ hơn openMinutes (ví dụ 22:00 -> 02:00)
            // Hiện tại ta giả định là trong cùng một ngày.
            if (closeMinutes <= openMinutes) {
                Toast.makeText(this, "Giờ đóng cửa phải sau giờ mở cửa (trong cùng một ngày).", Toast.LENGTH_SHORT).show();
                etCloseTime.requestFocus();
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Định dạng giờ không hợp lệ.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}