package com.example.prm392_gr5.Ui.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.admin.AdminMainActivity;
import com.example.prm392_gr5.Ui.booking.BookingHistoryActivity;
import com.example.prm392_gr5.Ui.owner.ManagePitchActivity;
import com.example.prm392_gr5.Ui.user.NotificationActivity;

public class AccountActivity extends Activity {

    TextView tvFullName, tvPhone, tvAvatar;
    LinearLayout btnBooking, btnAccountInfo, btnDeleteAccount, btnLogout;
    LinearLayout navAccount, navHome, navFavorite, navNotify;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        dbHelper = new DatabaseHelper(this);

        mappingViews();
        loadUserInfo();
        setupEvent();
        setupFooterNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void mappingViews() {
        tvFullName       = findViewById(R.id.tvFullName);
        tvPhone          = findViewById(R.id.tvPhone);
        tvAvatar         = findViewById(R.id.tvAvatar);

        btnBooking       = findViewById(R.id.btnBooking);
        btnAccountInfo   = findViewById(R.id.btnAccountInfo);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnLogout        = findViewById(R.id.btnLogout);

        navHome          = findViewById(R.id.navHome);
        navFavorite      = findViewById(R.id.navFavorite);
        navNotify        = findViewById(R.id.navNotify);
        navAccount       = findViewById(R.id.navAccount);
    }

    private void loadUserInfo() {
        String fullName = SharedPreferencesHelper.getFullName(this);
        String phone    = SharedPreferencesHelper.getSavedPhone(this);

        tvFullName.setText(fullName);
        tvPhone.setText(phone);

        if (!fullName.isEmpty()) {
            tvAvatar.setText(fullName.substring(0, 1).toUpperCase());
        }
    }

    private void setupEvent() {
        // ← Bật listener cho nút "Đặt sân của tôi"
        btnBooking.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, BookingHistoryActivity.class);
            startActivity(intent);
        });

        btnAccountInfo.setOnClickListener(v -> {
            startActivity(new Intent(this, AccountInfoActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất không?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        SharedPreferencesHelper.clear(this);
                        // Quay về LoginActivity và xóa tất cả Activity trong back stack
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa các Activity trước đó
                        startActivity(intent);

                        // Đảm bảo người dùng không quay lại màn hình tài khoản
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xoá tài khoản")
                    .setMessage("Bạn có chắc chắn muốn xoá tài khoản này?")
                    .setPositiveButton("Xoá", (dialog, which) -> deleteAccount())
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void deleteAccount() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String phone = SharedPreferencesHelper.getSavedPhone(this);
        db.delete("users", "phoneNumber = ?", new String[]{phone});

        Toast.makeText(this, "Đã xoá tài khoản", Toast.LENGTH_SHORT).show();
        SharedPreferencesHelper.clear(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void setupFooterNavigation() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        navFavorite.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng Yêu thích đang phát triển", Toast.LENGTH_SHORT).show()
        );
        navNotify.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });
        navAccount.setOnClickListener(v ->
                Toast.makeText(this, "Bạn đang ở trang Tài khoản", Toast.LENGTH_SHORT).show()
        );
    }
//    private void setupFooterNavigation() {
//        navHome.setOnClickListener(v -> {
//            // Lấy thông tin user hiện tại từ SharedPreferences hoặc session
//            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//            String userRole = sharedPreferences.getString("userRole", "user");
//            int userId = sharedPreferences.getInt("userId", -1);
//
//            // Điều hướng theo role
//            Intent intent;
//            switch (userRole) {
//                case "admin":
//                    intent = new Intent(this, AdminMainActivity.class);
//                    break;
//                case "owner":
//                    intent = new Intent(this, ManagePitchActivity.class);
//                    break;
//                case "user":
//                default:
//                    intent = new Intent(this, HomeActivity.class);
//                    break;
//            }
//
//            // Truyền thông tin user nếu cần
//            intent.putExtra("userId", userId);
//            intent.putExtra("userRole", userRole);
//
//            startActivity(intent);
//            finish();
//        });
//    }
}
