package com.example.prm392_gr5.Ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.*;

import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.R;

public class OtpForgotActivity extends Activity {

    TextView txtPhone, txtCountdown, txtResend;
    EditText[] otpInputs = new EditText[6];
    Button btnConfirm;

    String phone, newPassword, otpCode;
    UserRepository userRepo;

    CountDownTimer timer;
    boolean isOtpExpired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_forgot);

        userRepo = new UserRepository(this);
        getIntentData();
        mappingView();
        setupOtpBox();
        startCountDown();

        txtPhone.setText("Mã OTP đã gửi tới: " + phone);

        btnConfirm.setOnClickListener(v -> verifyOtp());

        txtResend.setOnClickListener(v -> {
            otpCode = String.valueOf((int) (100000 + Math.random() * 900000));
            NotificationHelper.showOtpNotification(this, "Mã OTP Đặt lại mật khẩu", "Mã OTP của bạn là: " + otpCode);
            startCountDown();
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        newPassword = intent.getStringExtra("newPassword");
        otpCode = intent.getStringExtra("otp");
    }

    private void mappingView() {
        txtPhone = findViewById(R.id.txtPhoneOtp);
        txtCountdown = findViewById(R.id.txtOtpCountdown);
        txtResend = findViewById(R.id.txtResendOtp);
        btnConfirm = findViewById(R.id.btnSubmitOtp);

        otpInputs[0] = findViewById(R.id.etOtp1);
        otpInputs[1] = findViewById(R.id.etOtp2);
        otpInputs[2] = findViewById(R.id.etOtp3);
        otpInputs[3] = findViewById(R.id.etOtp4);
        otpInputs[4] = findViewById(R.id.etOtp5);
        otpInputs[5] = findViewById(R.id.etOtp6);
    }

    private void setupOtpBox() {
        for (int i = 0; i < otpInputs.length - 1; i++) {
            int index = i;
            otpInputs[i].addTextChangedListener(new SimpleTextWatcher(() -> {
                if (otpInputs[index].getText().toString().length() == 1) {
                    otpInputs[index + 1].requestFocus();
                }
            }));
        }
    }

    private void startCountDown() {
        if (timer != null) {
            timer.cancel();
        }
        isOtpExpired = false;
        txtResend.setEnabled(false);
        txtResend.setTextColor(Color.GRAY);

        timer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                txtCountdown.setText("OTP sẽ hết hạn sau " + millisUntilFinished / 1000 + " giây");
            }

            public void onFinish() {
                isOtpExpired = true;
                txtCountdown.setText("OTP đã hết hạn");
                txtResend.setEnabled(true);
                txtResend.setTextColor(Color.BLUE);
            }
        }.start();
    }

    private void verifyOtp() {
        // Check if the OTP is expired
        if (isOtpExpired) {
            Toast.makeText(this, "Mã OTP đã hết hạn, vui lòng yêu cầu mã mới", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build the entered OTP from the EditTexts
        StringBuilder inputOtp = new StringBuilder();
        for (EditText e : otpInputs) {
            inputOtp.append(e.getText().toString());
        }

        // Compare entered OTP with the OTP sent
        if (!inputOtp.toString().equals(otpCode)) {
            Toast.makeText(this, "Sai mã OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reset the password if OTP is correct
        boolean ok = userRepo.resetPassword(phone, newPassword);
        if (ok) {
            Toast.makeText(this, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OtpForgotActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Số điện thoại không tồn tại", Toast.LENGTH_SHORT).show();
        }
    }

}
