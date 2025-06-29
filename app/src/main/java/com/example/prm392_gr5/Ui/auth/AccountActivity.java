package com.example.prm392_gr5.Ui.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Ui.user.HistoryActivity;

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
        tvFullName = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAvatar = findViewById(R.id.tvAvatar);

        btnBooking = findViewById(R.id.btnBooking);
        btnAccountInfo = findViewById(R.id.btnAccountInfo);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnLogout = findViewById(R.id.btnLogout);
        navHome = findViewById(R.id.navHome);
        navFavorite = findViewById(R.id.navFavorite);
        navNotify = findViewById(R.id.navNotify);
        navAccount = findViewById(R.id.navAccount);
    }

    private void loadUserInfo() {
        String fullName = SharedPreferencesHelper.getFullName(this);
        String phone = SharedPreferencesHelper.getSavedPhone(this);

        tvFullName.setText(fullName);
        tvPhone.setText(phone);

        // Avatar chữ cái đầu
        if (!fullName.isEmpty()) {
            tvAvatar.setText(fullName.substring(0, 1).toUpperCase());
        }
    }

    private void setupEvent() {

        btnBooking.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
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
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xoá tài khoản")
                    .setMessage("Bạn có chắc chắn muốn xoá tài khoản này?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        deleteAccount();
                    })
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

        navFavorite.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Yêu thích đang phát triển", Toast.LENGTH_SHORT).show();
        });

        navNotify.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Thông báo đang phát triển", Toast.LENGTH_SHORT).show();
        });

        navAccount.setOnClickListener(v -> {
            // Đang ở trang Tài khoản, có thể không cần xử lý hoặc làm mới lại
            // Hoặc showToast để thông báo
            Toast.makeText(this, "Bạn đang ở trang Tài khoản", Toast.LENGTH_SHORT).show();
        });
    }

}
