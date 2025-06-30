package com.example.prm392_gr5.Ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_gr5.R;
import com.example.prm392_gr5.Data.model.User;
import com.example.prm392_gr5.Data.repository.UserRepository;

public class AddEditUserActivity extends AppCompatActivity {
    private EditText etName, etPhone, etPass;
    private Spinner spRole;
    private Button btnSave;
    private UserRepository userRepo;
    private String mode;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_edit_user);

        etName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhoneNumber);
        etPass = findViewById(R.id.etPassword);
        spRole = findViewById(R.id.spRole);
        btnSave = findViewById(R.id.btnSave);

        userRepo = new UserRepository(this);
        mode = getIntent().getStringExtra("mode");

        // Setup spinner
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"user", "owner", "admin"});
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        // Nếu đang chỉnh sửa
        if ("edit".equals(mode)) {
            userId = getIntent().getIntExtra("userId", -1);
            if (userId != -1) {
                User u = userRepo.getUserById(userId);
                if (u != null) {
                    etName.setText(u.getFullName());
                    etPhone.setText(u.getPhoneNumber());
                    etPass.setText(u.getPassword());
                    spRole.setSelection(roleAdapter.getPosition(u.getRole()));
                } else {
                    Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            String role = spRole.getSelectedItem().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            User u = new User();
            u.setFullName(name);
            u.setPhoneNumber(phone);
            u.setPassword(pass);
            u.setRole(role);

            if ("edit".equals(mode)) {
                u.setId(userId);
                userRepo.updateUser(u);
            } else {
                // Tạo mới -> mặc định là active
                u.setIsActive(true);
                userRepo.addUser(u);
            }

            setResult(RESULT_OK);
            finish();
        });
    }
}
