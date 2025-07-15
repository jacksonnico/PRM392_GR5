package com.example.prm392_gr5.Ui.owner;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_gr5.Data.repository.PitchRepository;
import com.example.prm392_gr5.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddPitchActivity extends AppCompatActivity {
    private int ownerId;
    private ImageView btnBack;
    private EditText etPitchName;
    private EditText etPitchAddress;
    private TextView tvOpenTime, tvCloseTime;
    private EditText etFacilities;
    private EditText etDescription;
    private EditText etPhysicalDetails;
    private EditText etReferencePrice;
    private EditText etNotes;
    private EditText etImageUrl;
    private EditText etPhoneNumber;
    private Button btnSavePitch;
    private PitchRepository pitchRepository;
    private ActivityResultLauncher<Intent> mapPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pitch);
        pitchRepository = new PitchRepository(this);
        ownerId = 2; // Có thể lấy từ SharedPreferences hoặc Intent
        initViews();
        setClickListeners();
        setupMapPicker();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etPitchName = findViewById(R.id.et_pitch_name);
        etPitchAddress = findViewById(R.id.et_pitch_address);
        tvOpenTime = findViewById(R.id.tv_open_time);
        tvCloseTime = findViewById(R.id.tv_close_time);
        etFacilities = findViewById(R.id.et_facilities);
        etDescription = findViewById(R.id.et_description);
        etPhysicalDetails = findViewById(R.id.et_physical_details);
        etReferencePrice = findViewById(R.id.et_reference_price);
        etNotes = findViewById(R.id.et_notes);
        etImageUrl = findViewById(R.id.et_image_url);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnSavePitch = findViewById(R.id.btn_save_pitch);

        // Thiết lập time picker cho giờ mở/đóng cửa
        setupTimePickers();
    }

    private void setupTimePickers() {
        tvOpenTime.setFocusable(false);
        tvOpenTime.setClickable(true);
        tvOpenTime.setInputType(InputType.TYPE_NULL);
        // Đảm bảo có giá trị mặc định để tránh lỗi khi validate
        if (tvOpenTime.getText().toString().isEmpty()) {
            tvOpenTime.setText("07:00");
        }


        tvCloseTime.setFocusable(false);
        tvCloseTime.setClickable(true);
        tvCloseTime.setInputType(InputType.TYPE_NULL);
        // Đảm bảo có giá trị mặc định để tránh lỗi khi validate
        if (tvCloseTime.getText().toString().isEmpty()) {
            tvCloseTime.setText("22:00");
        }
    }

    private void setupMapPicker() {
        mapPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            double lat = data.getDoubleExtra("lat", 0.0);
                            double lng = data.getDoubleExtra("lng", 0.0);
                            String address = data.getStringExtra("address");

                            if (address != null && !address.isEmpty()) {
                                etPitchAddress.setText(address);
                            } else {
                                // Nếu không có address từ Intent, dùng Geocoder
                                String geocodedAddress = getAddressFromLatLng(lat, lng);
                                etPitchAddress.setText(geocodedAddress);
                            }
                        }
                    }
                });
    }

    private void setClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSavePitch.setOnClickListener(v -> savePitch());

        tvOpenTime.setOnClickListener(this::showTimePickerDialog);
        tvCloseTime.setOnClickListener(this::showTimePickerDialog);

        // Chỉ giữ lại sự kiện chọn địa chỉ từ bản đồ
        etPitchAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapPickerActivity.class);
            mapPickerLauncher.launch(intent);
        });
    }

    private String getAddressFromLatLng(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Vị trí không xác định";
    }

    private void showTimePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> setTimeFromPicker(v, selectedHour, selectedMinute),
                hour, minute, true);
        timePickerDialog.show();
    }

    private void setTimeFromPicker(View v, int hour, int minute) {
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        if (v.getId() == R.id.tv_open_time) {
            tvOpenTime.setText(formattedTime);
        } else if (v.getId() == R.id.tv_close_time) {
            tvCloseTime.setText(formattedTime);
        }
    }

    private void savePitch() {
        // Lấy dữ liệu từ các trường
        String name = etPitchName.getText().toString().trim();
        String address = etPitchAddress.getText().toString().trim();
        String openTime = tvOpenTime.getText().toString().trim();
        String closeTime = tvCloseTime.getText().toString().trim();
        String facilities = etFacilities.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String physicalDetails = etPhysicalDetails.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim(); // Lấy URL ảnh
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        // Xử lý giá
        Double price = parsePrice(etReferencePrice);
        if (price == null) {
            return; // Lỗi đã được hiển thị trong parsePrice
        }

        // Validate dữ liệu bắt buộc và định dạng
        if (!validateInput(name, address, openTime, closeTime, price, imageUrl,phoneNumber)) {
            return;
        }

        // Lưu vào database
        boolean success = pitchRepository.insertPitch(
                ownerId,
                name,
                address,
                price,
                openTime,
                closeTime,
                "", // phoneNumber - giữ nguyên nếu bạn chưa có
                imageUrl  // Truyền imageUrl vào đây
        );

        if (success) {
            Toast.makeText(this, "Thêm cơ sở thành công!", Toast.LENGTH_SHORT).show();

            // Trả về kết quả cho Activity trước
            Intent resultIntent = new Intent();
            resultIntent.putExtra("pitch_added", true);
            resultIntent.putExtra("pitch_name", name);
            setResult(RESULT_OK, resultIntent);

            finish();
        } else {
            Toast.makeText(this, "Lỗi khi thêm cơ sở. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private Double parsePrice(EditText priceField) {
        try {
            String priceStr = priceField.getText().toString().trim();

            if (priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập giá thuê sân.", Toast.LENGTH_SHORT).show();
                priceField.requestFocus();
                return null;
            }

            // Loại bỏ các ký tự không phải số và dấu chấm
            priceStr = priceStr.replaceAll("[^0-9.]", "");

            // Xử lý trường hợp chuỗi chỉ có dấu chấm hoặc rỗng sau khi loại bỏ ký tự
            if (priceStr.isEmpty() || priceStr.equals(".")) {
                Toast.makeText(this, "Giá thuê sân không hợp lệ. Vui lòng nhập số.", Toast.LENGTH_SHORT).show();
                priceField.requestFocus();
                return null;
            }

            double price = Double.parseDouble(priceStr);

            if (price <= 0) {
                Toast.makeText(this, "Giá thuê sân phải lớn hơn 0.", Toast.LENGTH_SHORT).show();
                priceField.requestFocus();
                return null;
            }

            return price;

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá thuê sân không hợp lệ. Vui lòng nhập số.", Toast.LENGTH_SHORT).show();
            priceField.requestFocus();
            return null;
        }
    }

    // Phương thức validate mới tập trung hơn
    private boolean validateInput(String name, String address, String openTime, String closeTime, Double price, String imageUrl, String phoneNumber) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên sân.", Toast.LENGTH_SHORT).show();
            etPitchName.requestFocus();
            return false;
        }
        if (name.length() < 3) {
            Toast.makeText(this, "Tên sân phải có ít nhất 3 ký tự.", Toast.LENGTH_SHORT).show();
            etPitchName.requestFocus();
            return false;
        }


        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ sân.", Toast.LENGTH_SHORT).show();
            etPitchAddress.requestFocus();
            return false;
        }

        if (openTime.isEmpty() || openTime.equals("00:00") && tvOpenTime.getText().toString().equals("00:00")) { // Thêm kiểm tra mặc định
            Toast.makeText(this, "Vui lòng chọn giờ mở cửa.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (closeTime.isEmpty() || closeTime.equals("00:00") && tvCloseTime.getText().toString().equals("00:00")) { // Thêm kiểm tra mặc định
            Toast.makeText(this, "Vui lòng chọn giờ đóng cửa.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate giờ mở/đóng cửa
        if (!validateBusinessHours(openTime, closeTime)) {
            return false;
        }

        if (price == null || price <= 0) {
            // Toast đã được hiển thị trong parsePrice
            return false;
        }

        // Validate URL ảnh
        if (imageUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập URL ảnh chính của sân.", Toast.LENGTH_SHORT).show();
            etImageUrl.requestFocus();
            return false;
        }
        if (!Patterns.WEB_URL.matcher(imageUrl).matches()) {
            Toast.makeText(this, "URL ảnh không hợp lệ. Vui lòng nhập đúng định dạng URL.", Toast.LENGTH_LONG).show();
            etImageUrl.requestFocus();
            return false;
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại liên hệ.", Toast.LENGTH_SHORT).show();
            etPhoneNumber.requestFocus();
            return false;
        }
        // Có thể dùng Patterns.PHONE hoặc regex tùy biến để validate số điện thoại
        // Ví dụ đơn giản: kiểm tra chỉ chứa số và có độ dài hợp lý (ví dụ 7-15 chữ số)
        if (!phoneNumber.matches("^[0-9]{7,15}$")) { // Chỉ chấp nhận 7-15 chữ số
            Toast.makeText(this, "Số điện thoại không hợp lệ. Vui lòng nhập từ 7 đến 15 chữ số.", Toast.LENGTH_LONG).show();
            etPhoneNumber.requestFocus();
            return false;
        }

        return true;
    }

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

            if (closeMinutes <= openMinutes) {
                Toast.makeText(this, "Giờ đóng cửa phải sau giờ mở cửa.", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;

        } catch (Exception e) {
            Toast.makeText(this, "Định dạng giờ không hợp lệ.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup nếu cần
    }
}

