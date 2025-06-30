package com.example.prm392_gr5.Ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prm392_gr5.Data.model.User;
import com.example.prm392_gr5.Data.repository.UserRepository;
import com.example.prm392_gr5.R;

public class AddEditOwnerActivity extends AppCompatActivity {
    private EditText etFullName, etUsername, etPassword;
    private Button btnSaveOwner;
    private UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_owner);

        userRepo = new UserRepository(this);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername); // Dùng cho phoneNumber
        etPassword = findViewById(R.id.etPassword);
        btnSaveOwner = findViewById(R.id.btnSaveOwner);

        btnSaveOwner.setOnClickListener(v -> saveOwner());
    }

    private void saveOwner() {
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etUsername.getText().toString().trim();  // username = phoneNumber
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userRepo.getUserByPhone(phoneNumber) != null) {
            Toast.makeText(this, "Số điện thoại đã được sử dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setPassword(password);
        newUser.setRole("owner");
        newUser.setIsActive(true);

        long id = userRepo.addUser(newUser);
        if (id != -1) {
            Toast.makeText(this, "Tạo chủ sân thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Trả kết quả về
            finish();
        } else {
            Toast.makeText(this, "Tạo chủ sân thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
