package com.example.prm392_gr5.Ui.auth;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.*;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.prm392_gr5.Data.model.User;
import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.R;

public class OtpActivity extends Activity {

    TextView tvPhone, tvCountDown, tvResend, tvResendLater;
    EditText[] otpInputs = new EditText[6];
    Button btnConfirm;

    String phone, fullName, password, role, otpCode;
    UserRepository userRepo;

    CountDownTimer timer;
    boolean isOtpExpired = false;
    String CHANNEL_ID = "otp_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        userRepo = new UserRepository(this);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        fullName = intent.getStringExtra("fullName");
        password = intent.getStringExtra("password");
        role = intent.getStringExtra("role");
        otpCode = intent.getStringExtra("otp");

        mappingView();
        setupOtpBox();
        startCountDown();
        sendOtpNotification(otpCode);

        tvPhone.setText(phone);

        btnConfirm.setOnClickListener(v -> checkOtp());
        tvResend.setOnClickListener(v -> resendOtp());
        tvResendLater.setOnClickListener(v -> {
            if (isOtpExpired) {
                resendOtp();
            }
        });
    }

    private void mappingView() {
        tvPhone = findViewById(R.id.tvPhone);
        tvCountDown = findViewById(R.id.tvCountDown);
        tvResend = findViewById(R.id.tvResend);
        tvResendLater = findViewById(R.id.tvResendLater);
        btnConfirm = findViewById(R.id.btnConfirm);

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
        if (timer != null) timer.cancel();
        isOtpExpired = false;
        tvResendLater.setEnabled(false);
        tvResendLater.setTextColor(Color.GRAY);

        timer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountDown.setText("OTP sẽ hết hạn sau " + millisUntilFinished / 1000 + " giây");
            }

            public void onFinish() {
                isOtpExpired = true;
                tvCountDown.setText("OTP đã hết hạn");
                tvResendLater.setEnabled(true);
                tvResendLater.setTextColor(Color.BLUE);
            }
        }.start();
    }

    private void resendOtp() {
        otpCode = String.valueOf((int) (100000 + Math.random() * 900000));
        sendOtpNotification(otpCode);
        startCountDown();
    }

    private void sendOtpNotification(String otp) {
        createNotificationChannel();
        String message = "Mã OTP của bạn là: " + otp;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_otp)
                .setContentTitle("OTP xác nhận")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            return;
        }
        notificationManager.notify(1001, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "OTP Channel";
            String description = "Channel gửi OTP";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void checkOtp() {
        StringBuilder userOtp = new StringBuilder();
        for (EditText e : otpInputs) {
            userOtp.append(e.getText().toString());
        }

        if (userOtp.toString().equals(otpCode)) {
            boolean ok = userRepo.register(new User(0, fullName, phone, password, role));
            if (ok) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Sai mã OTP", Toast.LENGTH_SHORT).show();
        }
    }
}
