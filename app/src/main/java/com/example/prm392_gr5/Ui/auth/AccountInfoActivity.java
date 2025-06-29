package com.example.prm392_gr5.Ui.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import com.example.prm392_gr5.Data.db.DatabaseHelper;
import com.example.prm392_gr5.R;

public class AccountInfoActivity extends Activity {

    EditText edtFullName, edtPhone;
    TextView tvAvatar;
    Button btnSave;
    LinearLayout btnChangePassword;

    DatabaseHelper dbHelper;

    String phone, fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        dbHelper = new DatabaseHelper(this);

        mappingViews();
        loadUserInfo();
        setupEvent();
    }

    private void mappingViews() {
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        tvAvatar = findViewById(R.id.tvAvatar);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
    }

    private void loadUserInfo() {
        phone = SharedPreferencesHelper.getSavedPhone(this);
        fullName = SharedPreferencesHelper.getFullName(this);

        edtFullName.setText(fullName);
        edtPhone.setText(phone);


        if (!fullName.isEmpty()) {
            tvAvatar.setText(fullName.substring(0, 1).toUpperCase());
        }
    }

    private void setupEvent() {
        btnSave.setOnClickListener(v -> {
            String newFullName = edtFullName.getText().toString().trim();
            String newPhone = edtPhone.getText().toString().trim();

            // Regex
            String nameRegex = "^[\\p{L} .'-]+$";
            String phoneRegex = "^0[3|5|7|8|9][0-9]{8}$";

            // Kiểm tra họ tên
            if (newFullName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newFullName.matches(nameRegex)) {
                Toast.makeText(this, "Họ tên không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra số điện thoại
            if (newPhone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPhone.matches(phoneRegex)) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update DB
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String oldPhone = SharedPreferencesHelper.getSavedPhone(this);

            ContentValues values = new ContentValues();
            values.put("fullName", newFullName);
            values.put("phoneNumber", newPhone);

            int result = db.update("users", values, "phoneNumber = ?", new String[]{oldPhone});
            if (result != -1) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                SharedPreferencesHelper.saveUserInfo(this, newFullName, newPhone, SharedPreferencesHelper.getRole(this));
                SharedPreferencesHelper.saveLogin(this, newPhone, SharedPreferencesHelper.getSavedPassword(this));
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });



        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            intent.putExtra("type", "changePassword");
            intent.putExtra("phone", phone);
            startActivity(intent);
        });
    }
}
