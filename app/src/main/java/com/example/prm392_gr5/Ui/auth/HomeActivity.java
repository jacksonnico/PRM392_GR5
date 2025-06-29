package com.example.prm392_gr5.Ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.prm392_gr5.R;

public class HomeActivity extends Activity {

    EditText edtSearch;
    ImageView btnSearch;

    LinearLayout navAccount, navHome, navFavorite, navNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mappingViews();
        setupEvent();
    }

    private void mappingViews() {
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        navAccount = findViewById(R.id.navAccount);
        navHome = findViewById(R.id.navHome);
        navFavorite = findViewById(R.id.navFavorite);
        navNotify = findViewById(R.id.navNotify);
    }

    private void setupEvent() {
        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đang tìm kiếm: " + keyword, Toast.LENGTH_SHORT).show();
            }
        });

        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Toast.makeText(this, "Bạn đang ở trang chủ", Toast.LENGTH_SHORT).show();
        });

        navFavorite.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Yêu thích đang phát triển", Toast.LENGTH_SHORT).show();
        });

        navNotify.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Thông báo đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }
}
