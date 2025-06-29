package com.example.prm392_gr5.Ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.R;

public class ForgotPasswordActivity extends Activity {

    EditText edtPhone, edtNewPass, edtConfirmPass;
    Button btnSubmit;
    UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userRepo = new UserRepository(this);

        edtPhone = findViewById(R.id.edtPhone);
        edtNewPass = findViewById(R.id.edtNewPassword);
        edtConfirmPass = findViewById(R.id.edtConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmitReset);

        btnSubmit.setOnClickListener(v -> handleSendOtp());
    }

    private void handleSendOtp() {
        String phone = edtPhone.getText().toString().trim();
        String newPass = edtNewPass.getText().toString().trim();
        String confirmPass = edtConfirmPass.getText().toString().trim();

        if (phone.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!phone.matches("^0[3|5|7|8|9][0-9]{8}$")) {
            showToast("Số điện thoại không hợp lệ");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showToast("Mật khẩu xác nhận không khớp");
            return;
        }

        if (!userRepo.isPhoneExists(phone)) {
            showToast("Số điện thoại chưa đăng ký");
            return;
        }

        // Sinh OTP
        String otpCode = String.valueOf((int) (100000 + Math.random() * 900000));

        // Thông báo OTP
        NotificationHelper.showOtpNotification(this, "Mã OTP Đặt lại mật khẩu", "Mã OTP của bạn là: " + otpCode);

        // Chuyển sang OTP
        Intent intent = new Intent(this, OtpForgotActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("newPassword", newPass);
        intent.putExtra("role", "user");
        intent.putExtra("otp", otpCode);
        startActivity(intent);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
